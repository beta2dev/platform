package ru.beta2.platform.taskgate.task;

import java.util.Set;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 11:26
 */
public interface TaskDataAdapter
{

    String getStringProperty(String key);

    void putStringProperty(String key, String value);

    Set<? extends CharSequence> getPropertyNames();

}
