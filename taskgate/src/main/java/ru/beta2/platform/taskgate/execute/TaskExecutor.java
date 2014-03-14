package ru.beta2.platform.taskgate.execute;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.*;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private final ServerLocator serverLocator;

    private ClientSessionFactory sessionFactory;
    private ClientSession session;

    private List<TargetHandler> handlers = new ArrayList<TargetHandler>();

    public TaskExecutor(ExecutorConfig cfg, ServerLocator serverLocator)
    {
        this.cfg = cfg;
        this.serverLocator = serverLocator;
    }

    @Override
    public void start()
    {
        log.trace("Starting TaskExecutor");

        try {
            log.trace("Create HornetQ session factory");
            sessionFactory = serverLocator.createSessionFactory();
        }
        catch (Exception e) {
            log.error("Error create HornetQ session factory", e);
            throw new LifecycleException("Error create HornetQ session factory", e);
        }

        try {
            log.trace("Create HornetQ session");
            session = sessionFactory.createSession();
        }
        catch (HornetQException e) {
            log.error("Error create HornetQ session", e);
            throw new LifecycleException("Error create HornetQ session", e);
        }

        try {
            activeTasksTargets();
        }
        catch (HornetQException e) {
            log.error("Error activate tasks targets", e);
            throw new LifecycleException("Error activate tasks targets", e);
        }

        try {
            log.trace("Start HornetQ session");
            session.start();
        }
        catch (HornetQException e) {
            log.error("Error start HornetQ session", e);
            throw new LifecycleException("Error start HornetQ session", e);
        }

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

        try {
            if (session != null) {
                log.trace("Stop and close HornetQ session");
                session.stop();
                session.close();
                session = null;
            }
            if (sessionFactory != null) {
                log.trace("Close HornetQ session factory");
                sessionFactory.close();
                sessionFactory = null;
            }
        }
        catch (HornetQException e) {
            log.error("Error close HornetQ resource", e);
            throw new LifecycleException("Error close HornetQ resource", e);
        }

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
            consumer = session.createConsumer(cfg.getQueue());
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
                    log.error("Error acknoledge message", e);
//                    throw new TaskExecuteException("Error acknoledge message", e);
                }
            }
        }

        @Override
        public String toString()
        {
            return "TargetHandler{name=" + cfg.getName() + ",queue" + cfg.getQueue() + '}';
        }

        private String getTaskResourceURL(TaskDescriptor task)
        {
            String base = cfg.getTaskBaseURL().trim();
            return base + (base.endsWith("/") ? "" : "/") + task.getTaskURLPath();
        }

        private HttpResponse executeHttpRequest(ClientMessage message) throws IOException, HornetQException
        {
            log.trace("Prepare HTTP request");
            TaskDescriptor t = new TaskDescriptor(message);

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
            else if (t.hasTaskParameters()) {
                log.trace("There is task parameters");
                req.bodyForm(t.getTaskParameters());
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

class TaskDescriptor
{

    private static final SimpleString PARAM_PREFIX = SimpleString.toSimpleString("param.");
    private static final int PARAM_PREFIX_LENGTH = PARAM_PREFIX.length();

    private final ClientMessage msg;

    private Collection<NameValuePair> parameters;

    TaskDescriptor(ClientMessage msg)
    {
        this.msg = msg;
    }

    String getTaskURLPath()
    {
        String url;
        String id = msg.getStringProperty("task.id");
        if (id != null) {
            url = "id/" + id;
        }
        else {
            url = msg.getStringProperty("task.class");
        }

        String m = msg.getStringProperty("task.method");
        return url + (m != null ? "/" + m : "");
    }

    boolean hasBodyPayload()
    {
        return msg.getBodySize() > 0;
    }

    ContentType getBodyContentType()
    {
        String ct = msg.getStringProperty("task.mimeType");
        if (ct != null) {
            return ContentType.create(ct, msg.getStringProperty("task.charset"));
        }
        return null;
    }

    boolean hasTaskParameters()
    {
        return !getTaskParameters().isEmpty();
    }

    Collection<NameValuePair> getTaskParameters()
    {
        if (parameters == null) {
            for (SimpleString p : msg.getPropertyNames()) {
                if (p.startsWith(PARAM_PREFIX)) {
                    if (parameters == null) {
                        parameters = new ArrayList<NameValuePair>();
                    }
                    parameters.add(new BasicNameValuePair(p.subSequence(PARAM_PREFIX_LENGTH, p.length()).toString(), msg.getStringProperty(p)));
                }
            }
            if (parameters == null) {
                parameters = Collections.emptyList();
            }
        }
        return parameters;
    }
}
