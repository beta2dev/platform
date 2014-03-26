package ru.beta2.platform.taskgate.execute;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.api.core.client.ServerLocator;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;
import ru.beta2.platform.hornetq.util.SingleSessionHelper;
import ru.beta2.platform.taskgate.task.ClientMessageTaskAdapter;
import ru.beta2.platform.taskgate.task.TaskDescriptor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 14:58
 */
public class TaskExecutor implements Startable
{

    private final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    private final ExecutorConfig cfg;
    private final SingleSessionHelper sessionHelper;

    private List<TargetHandler> handlers = new ArrayList<TargetHandler>();

    public TaskExecutor(ExecutorConfig cfg, ServerLocator serverLocator)
    {
        this.cfg = cfg;
        this.sessionHelper = new SingleSessionHelper(log, serverLocator);
    }

    @Override
    public void start()
    {
        log.trace("Starting TaskExecutor");

        sessionHelper.create();

        try {
            activeTasksTargets();
        }
        catch (HornetQException e) {
            log.error("Error activate tasks targets", e);
            throw new LifecycleException("Error activate tasks targets", e);
        }

        sessionHelper.start();

        log.info("TaskExecutor started");
    }

    @Override
    public void stop()
    {
        log.trace("Stopping TaskExecutor");

        try {
            for (TargetHandler h : handlers) {
                h.close();
            }
        }
        catch (Exception e) {
            log.error("Error close target handler", e);
            throw new LifecycleException("Error close target handler", e);
        }
        handlers.clear();

        sessionHelper.stopAndClose();

        log.info("TaskExecutor stopped");
    }

    private void activeTasksTargets() throws HornetQException
    {
        log.trace("Activate tasks targets");
        for (TargetConfig tcfg : cfg.getTargets()) {
            if (tcfg.isDisabled()) {
                log.debug("Task target '{}' is disabled", tcfg.getName());
                continue;
            }
            handlers.add(new TargetHandler(tcfg));
        }
        log.debug("Targets activated: {}", handlers);
    }

    class TargetHandler implements AutoCloseable, MessageHandler
    {

        private static final String TASK_ID_HEADER = "X-B2-Task-Id";

        private final TargetConfig cfg;

        private final ClientConsumer consumer;

        TargetHandler(TargetConfig cfg) throws HornetQException
        {
            this.cfg = cfg;
            consumer = sessionHelper.getSession().createConsumer(cfg.getQueue());
            consumer.setMessageHandler(this);
        }

        @Override
        public void close() throws Exception
        {
            consumer.close();
        }

        @Override
        public void onMessage(ClientMessage message)
        {
            log.debug("Message received: {}", message);

            boolean ack;
            try {
                ack = processHttpResponse(
                        executeHttpRequest(message)
                );
            }
            catch (IOException e) {
                log.error("IO error executing task", e);
                ack = cfg.isAckOnException();
//                throw new TaskExecuteException("IO error executing task", e);
            }
            catch (HornetQException e) {
                log.error("HornetQ error retrieving task", e);
                ack = cfg.isAckOnException();
//                throw new TaskExecuteException("HornetQ error retrieving task", e);
            }

            if (ack) {
                log.trace("Acknowledge message");
                try {
                    message.acknowledge();
                }
                catch (HornetQException e) {
                    log.error("Error acknowledge message", e);
//                    throw new TaskExecuteException("Error acknowledge message", e);
                }
            }
        }

        @Override
        public String toString()
        {
            return "TargetHandler{name=" + cfg.getName() + ",queue" + cfg.getQueue() + '}';
        }

        private String getTaskResourceURL(ExecuteTaskDescriptor task)
        {
            String base = cfg.getTaskBaseURL().trim();
            return base + (base.endsWith("/") ? "" : "/") + task.getTaskURLPath();
        }

        private HttpResponse executeHttpRequest(ClientMessage message) throws IOException, HornetQException
        {
            log.trace("Prepare HTTP request");
            ExecuteTaskDescriptor t = new ExecuteTaskDescriptor(message);

            Request req = Request.Post(getTaskResourceURL(t))
                    .connectTimeout(cfg.getConnectTimeout())
                    .socketTimeout(cfg.getSocketTimeout());

            if (t.hasBodyPayload()) { // ??? нужно ли нам закрывать pis и/или pos - скорее всего не нужно (так как в HTTPClient есть close input stream'a)
                log.trace("There is body payload");
                PipedInputStream pis = new PipedInputStream();
                PipedOutputStream pos = new PipedOutputStream(pis);

                message.setOutputStream(pos);

                ContentType ct = t.getBodyContentType();
                log.debug("Pass body stream, contentType={}", ct);
                if (ct != null) {
                    req.bodyStream(pis, ct);
                }
                else {
                    req.bodyStream(pis);
                }
            }
            else if (t.hasParameters()) {
                log.trace("There is task parameters");
                req.bodyForm(t.getParameters());
            }

            log.trace("Execute HTTP request");
            return req.execute().returnResponse();
        }

        private boolean processHttpResponse(HttpResponse resp)
        {
            log.trace("Process HTTP response");
            int status = resp.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                Header taskId = resp.getFirstHeader(TASK_ID_HEADER);
                if (taskId == null) {
                    log.debug("Response status is OK, TaskID is absent");
                }
                else {
                    log.debug("Response status is OK, TaskID={}", taskId.getValue());
                }
            }
            else {
                log.warn("Response status is NOT 200: {}", resp.getStatusLine());
            }

            return cfg.isAckOnHttpStatus(status);
        }

    }
}

class ExecuteTaskDescriptor extends TaskDescriptor
{

    private final ClientMessage msg;

    ExecuteTaskDescriptor(ClientMessage msg)
    {
        super(new ClientMessageTaskAdapter(msg));
        this.msg = msg;
    }

    String getTaskURLPath()
    {
        String url;
        String id = getTaskId();
        if (id != null) {
            url = "id/" + id;
        }
        else {
            url = getTaskClass();
        }

        String m = getTaskMethod();
        return url + (m != null ? "/" + m : "");
    }

    boolean hasBodyPayload()
    {
        return msg.getBodySize() > 0;
    }

    ContentType getBodyContentType()
    {
        String ct = getBodyMimeType();
        if (ct != null) {
            return ContentType.create(ct, getBodyCharset());
        }
        return null;
    }
}
