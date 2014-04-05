package ru.beta2.platform.mongosync.emitter;

/**
 * User: inc
 * Date: 05.04.14
 * Time: 11:42
 */
public class TransmitException extends Exception
{

    public TransmitException(String message)
    {
        super(message);
    }

    public TransmitException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
