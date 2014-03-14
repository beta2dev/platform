package ru.beta2.platform.core.util;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 13:22
 */
public class LifecycleException extends RuntimeException
{
    public LifecycleException(String message)
    {
        super(message);
    }

    public LifecycleException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
