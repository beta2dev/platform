package ru.beta2.platform.mongosync.protocol;

import org.bson.BasicBSONObject;

/**
 * User: inc
 * Date: 05.04.14
 * Time: 0:39
 */
public class OplogRecordMessage extends ProtocolMessage
{

    private BasicBSONObject oplogRecord;

    public OplogRecordMessage(BasicBSONObject oplogRecord)
    {
        this.oplogRecord = oplogRecord;
    }

    public BasicBSONObject getOplogRecord()
    {
        return oplogRecord;
    }
}
