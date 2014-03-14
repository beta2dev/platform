package ru.beta2.platform.scheduler;

import org.quartz.JobDetail;

import java.io.Serializable;
import java.util.Map;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 19:12
 */
public class JobDescriptor implements Serializable
{

    private String group;
    private String name;

    private String description;

    private String type;

    private Map<?,?> data;

    public JobDescriptor()
    {
    }

    public JobDescriptor(JobDetail detail)
    {
        group = detail.getKey().getGroup();
        name = detail.getKey().getName();

        description = detail.getDescription();

        type = detail.getJobClass().getName();

        data = detail.getJobDataMap();
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getType()
    {
        return type;
    }

    public Map<?, ?> getData()
    {
        return data;
    }
}
