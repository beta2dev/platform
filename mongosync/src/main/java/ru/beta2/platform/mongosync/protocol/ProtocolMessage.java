package ru.beta2.platform.mongosync.protocol;

import org.hornetq.api.core.client.ClientMessage;

/**
 * User: inc
 * Date: 02.04.14
 * Time: 14:04
 */
public abstract class ProtocolMessage
{

    public abstract ClientMessage toHornetQMessage(ClientMessage message);

}
