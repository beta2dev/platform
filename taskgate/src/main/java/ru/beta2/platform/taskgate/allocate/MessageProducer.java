package ru.beta2.platform.taskgate.allocate;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Message;
import org.hornetq.api.core.client.ClientMessage;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 15:19
 */
public interface MessageProducer
{

    ClientMessage createMessage();

    void send(String address, Message msg) throws HornetQException;

}
