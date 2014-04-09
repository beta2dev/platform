package ru.beta2.platform.mongosync.emitter.oplog;

import org.bson.BasicBSONObject;

/**
 * User: inc
 * Date: 08.04.14
 * Time: 19:55
 */
public interface OplogHandler
{
    void processOplogRecord(BasicBSONObject record) throws ProcessOplogException;
}
