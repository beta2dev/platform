package ru.beta2.platform.scheduler;

/**
 * User: inc
 * Date: 20.03.14
 * Time: 12:57
 */
public class SchedulerManageException extends RuntimeException
{
    public SchedulerManageException(String message)
    {
        super(message);
    }

    public SchedulerManageException(String message, Throwable cause)
    {
        super(message + ": " + cause.getMessage(), cause);
    }
}
