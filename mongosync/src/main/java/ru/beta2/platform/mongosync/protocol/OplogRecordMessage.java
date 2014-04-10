package ru.beta2.platform.mongosync.protocol;

import org.bson.BSON;
import org.bson.BSONObject;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * User: inc
 * Date: 05.04.14
 * Time: 0:39
 */
public class OplogRecordMessage extends ProtocolMessage
{

    public static final String CMDNAME = "oplog";

    private BSONObject oplogRecord;

    public OplogRecordMessage(BSONObject oplogRecord)
    {
        this.oplogRecord = oplogRecord;
    }

    public OplogRecordMessage(ClientMessage msg) throws HornetQException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream(msg.getBodySize());
        msg.saveToOutputStream(os);
        oplogRecord = BSON.decode(os.toByteArray());
    }

    public BSONObject getOplogRecord()
    {
        return oplogRecord;
    }

    @Override
    public ClientMessage toHornetQMessage(ClientMessage message)
    {
        message.putStringProperty("cmd", CMDNAME);
        message.setBodyInputStream(new ByteArrayInputStream(BSON.encode(oplogRecord)));
        return message;
    }

    @Override
    public String toString()
    {
        return "OplogRecordMessage{" +
                "oplogRecord=" + oplogRecord +
                '}';
    }
}
