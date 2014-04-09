package ru.beta2.platform.mongosync.emitter.emit;

/**
 * User: inc
 * Date: 08.04.14
 * Time: 20:01
 */
public class EmitListenerException extends Exception
{
    public EmitListenerException(String message)
    {
        super(message);
    }
    public EmitListenerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
