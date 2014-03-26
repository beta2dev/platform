package ru.beta2.platform.taskgate.allocate;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Message;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ServerLocator;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;
import ru.beta2.platform.hornetq.util.SingleSessionHelper;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 15:21
 */
public class SingleSessionMessageProducer implements MessageProducer, Startable
{

    private final Logger log = LoggerFactory.getLogger(SingleSessionMessageProducer.class);

    private final SingleSessionHelper sessionHelper;

    private ClientProducer clientProducer;

    public SingleSessionMessageProducer(ServerLocator serverLocator)
    {
        sessionHelper = new SingleSessionHelper(log, serverLocator);
    }

    @Override
    public synchronized ClientMessage createMessage()
    {
        return sessionHelper.getSession().createMessage(true);
    }

    @Override
    public synchronized void send(String address, Message msg) throws HornetQException
    {
        clientProducer.send(address, msg);
    }

    @Override
    public void start()
    {
        log.trace("Start MessageProducer");
        sessionHelper.create();

        log.trace("Create ClientProducer");
        try {
            clientProducer = sessionHelper.getSession().createProducer();
        }
        catch (HornetQException e) {
            log.error("Error create producer", e);
            throw new LifecycleException("Error create producer", e);
        }
    }

    @Override
    public void stop()
    {
        log.trace("Stop MessageProducer");

        if (clientProducer != null) {
            try {
                clientProducer.close();
            }
            catch (HornetQException e) {
                log.error("Error stop producer", e);
                throw new LifecycleException("Error stop producer", e);
            }
            clientProducer = null;
        }

        sessionHelper.stopAndClose();
    }
}
