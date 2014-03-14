package ru.beta2.platform.scheduler;

import org.quartz.JobKey;

import java.util.List;
import java.util.Set;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 13:50
 */
public interface SchedulerCover
{

    List<String> getAvailableJobTypes();

    Set<JobKey> getJobKeys();

    JobDescriptor getJob(JobKey key);

    void addJob(JobDescriptor job);

    void updateJob(JobKey key, JobDescriptor job);

}
