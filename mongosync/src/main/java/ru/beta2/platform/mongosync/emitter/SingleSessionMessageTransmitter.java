package ru.beta2.platform.mongosync.emitter;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ServerLocator;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;
import ru.beta2.platform.hornetq.util.SingleSessionHelper;
import ru.beta2.platform.mongosync.protocol.ProtocolMessage;

import java.util.Set;

/**
 * User: inc
 * Date: 05.04.14
 * Time: 1:47
 */
public class SingleSessionMessageTransmitter implements MessageTransmitter, Startable
{

    private final Logger log = LoggerFactory.getLogger(SingleSessionMessageTransmitter.class);
    // todo !!! add more logging

    private final SingleSessionHelper sessionHelper;

    private ClientProducer producer;

    public SingleSessionMessageTransmitter(ServerLocator serverLocator)
    {
        sessionHelper = new SingleSessionHelper(log, serverLocator);
    }

    @Override
    public synchronized void sendMessage(String address, ProtocolMessage message) throws TransmitException
    {
        try {
            producer.send(address, toClientMessage(message));
        }
        catch (HornetQException e) {
            log.error("Error send message", e);
            throw new TransmitException("Error send message", e);
        }
    }

    @Override
    public synchronized void sendMessage(Set<String> addresses, ProtocolMessage message) throws TransmitException
    {
        try {
            for (String address : addresses) {
                producer.send(address, toClientMessage(message));
            }
        }
        catch (HornetQException e) {
            log.error("Error send message", e);
            throw new TransmitException("Error send message", e);
        }
    }

    @Override
    public void start()
    {
        sessionHelper.create();
        try {
            producer = sessionHelper.getSession().createProducer();
        }
        catch (HornetQException e) {
            log.error("Error create ClientProducer", e);
            throw new LifecycleException("Error create ClientProducer", e);
        }
    }

    @Override
    public void stop()
    {
        try {
            producer.close();
            producer = null;
        }
        catch (HornetQException e) {
            log.warn("Error close ClientProducer, ignore it", e);
        }

        sessionHelper.stopAndClose();
    }

    private ClientMessage toClientMessage(ProtocolMessage message)
    {
        return message.toHornetQMessage(sessionHelper.getSession().createMessage(true));
    }
}
