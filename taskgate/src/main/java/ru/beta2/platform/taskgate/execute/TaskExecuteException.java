package ru.beta2.platform.taskgate.execute;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 20:24
 */
public class TaskExecuteException extends RuntimeException
{
    public TaskExecuteException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TaskExecuteException(Throwable cause)
    {
        super(cause);
    }
}
