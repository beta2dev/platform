package ru.beta2.platform.mongosync;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 0:39
 */
public interface MongoSyncServerControl
{

    void restartEmitter();

    void restartReceiver();

}
