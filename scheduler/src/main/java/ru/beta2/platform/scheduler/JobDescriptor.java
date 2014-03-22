package ru.beta2.platform.scheduler;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.utils.Key;

import java.io.Serializable;
import java.util.Map;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 19:12
 */
public class JobDescriptor extends ObjectDescriptor implements Serializable
{

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

    @Override
    public String toString()
    {
        return "JobDescriptor{" +
                "group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

    public JobDetail toJobDetail() throws ClassNotFoundException, IllegalClassException
    {
        JobBuilder b = JobBuilder
                .newJob(getJobClass(this.getType()))
                .withIdentity(this.getNameSafe(), this.getGroupSafe())
                .withDescription(this.getDescription())
                .storeDurably()
                ;
        if (this.getData() != null && !this.getData().isEmpty()) {
            b.setJobData(new JobDataMap(this.getData()));
        }
        return b.build();
    }

    private Class<? extends Job> getJobClass(String type) throws ClassNotFoundException, IllegalClassException
    {
        Class cl = Class.forName(type);
        if (!Job.class.isAssignableFrom(cl)) {
            throw new IllegalClassException(Job.class, cl);
        }
        return cl;
    }

}
