package ru.beta2.platform.taskgate.task;

import org.quartz.JobDataMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 12:51
 */
public class JobDataMapTaskAdapter implements TaskDataAdapter
{

    private final JobDataMap jdm;

    public JobDataMapTaskAdapter(JobDataMap jdm)
    {
        this.jdm = jdm;
    }

    @Override
    public String getStringProperty(String key)
    {
        return jdm.getString(key);
    }

    @Override
    public void putStringProperty(String key, String value)
    {
        jdm.put(key, value);
    }

    @Override
    public Set<? extends CharSequence> getPropertyNames()
    {
        HashSet<String> keys = new HashSet<String>();
        Collections.addAll(keys, jdm.getKeys());
        return keys;
    }
}
