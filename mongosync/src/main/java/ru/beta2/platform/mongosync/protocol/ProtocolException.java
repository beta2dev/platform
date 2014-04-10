package ru.beta2.platform.mongosync.protocol;

/**
 * User: Inc
 * Date: 10.04.2014
 * Time: 20:26
 */
public class ProtocolException extends Exception
{
    public ProtocolException(String message)
    {
        super(message);
    }

    public ProtocolException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
