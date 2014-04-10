package ru.beta2.platform.mongosync.protocol;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;

/**
 * User: inc
 * Date: 02.04.14
 * Time: 14:04
 */
public abstract class ProtocolMessage
{

    public abstract ClientMessage toHornetQMessage(ClientMessage message);

    public static ProtocolMessage createProtocolMessage(ClientMessage message) throws HornetQException, ProtocolException
    {
        String cmd = message.getStringProperty("cmd");
        if (CloneCollectionsMessage.CMDNAME.equals(cmd)) {
            return new CloneCollectionsMessage(message);
        }
        else if (OplogRecordMessage.CMDNAME.equals(cmd)) {
            return new OplogRecordMessage(message);
        }
        throw new ProtocolException("Unknown protocol command: " + cmd);
    }
}
