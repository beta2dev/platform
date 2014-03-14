package ru.beta2.platform.taskgate.execute;

/**
 * User: Inc
 * Date: 11.03.14
 * Time: 14:49
 */
public interface ProcessingConfig
{

    int getConnectTimeout();

    int getSocketTimeout();

    boolean isAckOnException();

    boolean isAckOnHttpStatus(int status);

}
