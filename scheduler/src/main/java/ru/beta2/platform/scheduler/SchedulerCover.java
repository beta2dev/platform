package ru.beta2.platform.scheduler;

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

    Set<ObjectKey> getJobKeys();

    JobDescriptor getJob(ObjectKey key);

    void addJob(JobDescriptor job);

    void updateJob(ObjectKey key, JobDescriptor job);

    List<TriggerInfo> getJobTriggers(ObjectKey jobKey);

    void addTrigger(ObjectKey jobKey, TriggerDescriptor trigger);

    void updateTrigger(ObjectKey triggerKey, TriggerDescriptor trigger);

    TriggerDescriptor getTrigger(ObjectKey key);

    void pauseTrigger(ObjectKey key);

    void resumeTrigger(ObjectKey key);

    void deleteTrigger(ObjectKey key);

    // todo DEFFERED add pause/resume job

    /**
     *
     * @param jobKey optional
     * @param limit optional
     * @return log in descending order (most recent return first)
     */
    List<JobExecutionInfo> getJobExecutionLog(ObjectKey jobKey, Integer limit);

}
