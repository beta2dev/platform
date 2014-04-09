package ru.beta2.platform.mongosync.emitter.oplog;

/**
 * User: inc
 * Date: 08.04.14
 * Time: 19:59
 */
public class ProcessOplogException extends Exception
{
    public ProcessOplogException(String message)
    {
        super(message);
    }

    public ProcessOplogException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
