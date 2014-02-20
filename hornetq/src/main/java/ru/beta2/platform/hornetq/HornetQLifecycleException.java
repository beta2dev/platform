package ru.beta2.platform.hornetq;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 19:54
 */
public class HornetQLifecycleException extends RuntimeException
{
    public HornetQLifecycleException(String message)
    {
        super(message);
    }

    public HornetQLifecycleException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
