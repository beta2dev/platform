package ru.beta2.platform.mongosync.emitter.emit;

/**
 * User: inc
 * Date: 08.04.14
 * Time: 20:01
 */
public class EmitLifecycleException extends Exception
{
    public EmitLifecycleException(String message)
    {
        super(message);
    }
    public EmitLifecycleException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
