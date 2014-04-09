package ru.beta2.platform.mongosync.emitter.emit;

/**
 * User: Inc
 * Date: 09.04.2014
 * Time: 12:01
 */
public class EmitException extends RuntimeException
{
    public EmitException(String message)
    {
        super(message);
    }

    public EmitException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
