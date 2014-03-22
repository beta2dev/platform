package ru.beta2.platform.scheduler;

import org.apache.commons.lang.IllegalClassException;
import org.picocontainer.Startable;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.undercover.UndercoverService;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 14:08
 */
public class QuartzSchedulerCover implements SchedulerCover, Startable
{

    private final Logger log = LoggerFactory.getLogger(QuartzSchedulerCover.class);

    private final SchedulerConfig cfg;
    private final UndercoverService undercoverService;
    private final Scheduler scheduler;
    private final PlatformJobFactory jobFactory;
    private final JobExecutionMonitor monitor;

    private HandlerRegistration undercoverRegistration;

    public QuartzSchedulerCover(SchedulerConfig cfg, UndercoverService undercoverService, Scheduler scheduler,
                                PlatformJobFactory jobFactory, JobExecutionMonitor monitor)
    {
        this.cfg = cfg;
        this.undercoverService = undercoverService;
        this.scheduler = scheduler;
        this.jobFactory = jobFactory;
        this.monitor = monitor;
    }

    @Override
    public void start()
    {
        log.trace("Register undercover handler");
        undercoverRegistration = undercoverService.registerCover("/scheduler", SchedulerCover.class, this);
    }

    @Override
    public void stop()
    {
        log.trace("Remove undercover handler");
        undercoverRegistration.removeHandler();
        undercoverRegistration = null;
    }

    @Override
    public List<String> getAvailableJobTypes()
    {
        List<String> types = new ArrayList<String>();
        for (Class<? extends Job> cl : jobFactory.getRegisteredJobTypes()) {
            types.add(cl.getName());
        }
        return types;
    }

    @Override
    public Set<ObjectKey> getJobKeys()
    {
        try {
            Set<JobKey> jks = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            Set<ObjectKey> oks = new HashSet<ObjectKey>(jks.size());
            for (JobKey jk : jks) {
                oks.add(new ObjectKey(jk.getName(), jk.getGroup()));
            }
            return oks;
        }
        catch (SchedulerException e) {
            log.error("Error get job keys", e);
            throw new SchedulerManageException("Error get job keys", e);
        }
    }

    @Override
    public JobDescriptor getJob(ObjectKey key)
    {
        log.debug("Get job, key={}", key);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey(key));
            return jobDetail != null ? new JobDescriptor(jobDetail) : null;
        }
        catch (SchedulerException e) {
            log.error("Error get job by key", e);
            throw new SchedulerManageException("Error get job by key", e);
        }
    }

    @Override
    public void addJob(JobDescriptor job)
    {
        log.debug("Add job: {}", job);

        try {
            scheduler.addJob(job.toJobDetail(), false);
        }
        catch (SchedulerException e) {
            log.error("Error add job", e);
            throw new SchedulerManageException("Error add job", e);
        }
        catch (ClassNotFoundException e) {
            log.error("Class not found for job", e);
            throw new SchedulerManageException("Class not found for job", e);
        }
        catch (IllegalClassException e) {
            log.error("Class is not job", e);
            throw new SchedulerManageException("Class is not job", e);
        }
    }

    @Override
    public void updateJob(ObjectKey key, JobDescriptor job)
    {
        log.debug("Update job: key={}, job={}", key, job);

        JobKey oldKey = jobKey(key);
        JobKey newKey = JobKey.jobKey(job.getNameSafe(), job.getGroupSafe());

        JobDetail jobDetail;
        try {
            jobDetail = job.toJobDetail();
        }
        catch (ClassNotFoundException e) {
            log.error("Class not found for job", e);
            throw new SchedulerManageException("Class not found for job", e);
        }
        catch (IllegalClassException e) {
            log.error("Class is not job", e);
            throw new SchedulerManageException("Class is not job", e);
        }

        if (oldKey.equals(newKey)) {
            log.trace("Replace job with same key");
            try {
                scheduler.addJob(jobDetail, true);
            }
            catch (SchedulerException e) {
                log.error("Error replace job with same key", e);
                throw new SchedulerManageException("Error replace job with same key", e);
            }
        }
        else {
            log.trace("Replace job with different key");

            try {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(oldKey);

                boolean deleteResult = scheduler.deleteJob(oldKey);
                log.debug("Job delete result: jobKey={}, deleteResult={}", oldKey, deleteResult);

                log.trace("Add job");
                scheduler.addJob(jobDetail, false);

                if (triggers != null && !triggers.isEmpty()) {
                    log.debug("Copy triggers for job: {}", triggers);
                    // чтобы это сработало, нужно все равно триггеры переделать на новый jobKey
//                    scheduler.scheduleJob(jobDetail, new HashSet<Trigger>(triggers), false);
                    for (Trigger tr : triggers) {
                        scheduler.scheduleJob(tr.getTriggerBuilder().forJob(newKey).build());
                    }
                }
            }
            catch (SchedulerException e) {
                log.error("Error replace job with different key", e);
                throw new SchedulerManageException("Error replace job with different key", e);
            }
        }
    }

    @Override
    public List<TriggerInfo> getJobTriggers(ObjectKey jobKey)
    {
        log.trace("Get triggers for job {}", jobKey);
        try {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey(jobKey));
            List<TriggerInfo> dd = new ArrayList<TriggerInfo>(triggers.size());
            for (Trigger tr : triggers) {
                TriggerInfo d = new TriggerInfo(tr);
                d.setState(scheduler.getTriggerState(tr.getKey()));
                dd.add(d);
            }
            return dd;
        }
        catch (SchedulerException e) {
            log.error("Error get job triggers", e);
            throw new SchedulerManageException("Error get job triggers", e);
        }
    }

    @Override
    public void addTrigger(ObjectKey jobKey, TriggerDescriptor trigger)
    {
        log.debug("Add trigger: jobKey={}, trigger={}", jobKey, trigger);
        try {
            scheduler.scheduleJob(trigger.toTrigger(jobKey(jobKey), cfg.getDefaultTriggerTimeZone()));
        }
        catch (SchedulerException e) {
            log.error("Error add trigger", e);
            throw new SchedulerManageException("Error add trigger", e);
        }
    }

    @Override
    public void updateTrigger(ObjectKey triggerKey, TriggerDescriptor trigger)
    {
        log.debug("Update trigger: key={}, trigger={}", triggerKey, trigger);
        try {
            TriggerKey oldkey = triggerKey(triggerKey);
            Trigger oldtr = scheduler.getTrigger(oldkey);
            Trigger.TriggerState oldstate = scheduler.getTriggerState(oldkey);
            Trigger newtr = trigger.toTrigger(oldtr.getJobKey(), cfg.getDefaultTriggerTimeZone());
            scheduler.rescheduleJob(oldkey, newtr);
            if (Trigger.TriggerState.PAUSED.equals(oldstate)) {
                log.trace("Pause rescheduled trigger");
                scheduler.pauseTrigger(newtr.getKey());
            }
        }
        catch (SchedulerException e) {
            log.error("Error update trigger", e);
            throw new SchedulerManageException("Error update trigger", e);
        }
    }

    @Override
    public TriggerDescriptor getTrigger(ObjectKey key)
    {
        log.trace("Get trigger {}", key);
        try {
            Trigger tr = scheduler.getTrigger(triggerKey(key));
            return tr != null ? new TriggerDescriptor(tr) : null;
        }
        catch (SchedulerException e) {
            log.error("Error get trigger", e);
            throw new SchedulerManageException("Error get trigger", e);
        }
    }

    @Override
    public void pauseTrigger(ObjectKey key)
    {
        log.debug("Pause trigger {}", key);
        try {
            scheduler.pauseTrigger(triggerKey(key));
        }
        catch (SchedulerException e) {
            log.error("Error pause trigger", e);
            throw new SchedulerManageException("Error pause trigger", e);
        }
    }

    @Override
    public void resumeTrigger(ObjectKey key)
    {
        log.debug("Resume trigger {}", key);
        try {
            scheduler.resumeTrigger(triggerKey(key));
        }
        catch (SchedulerException e) {
            log.error("Error resume trigger", e);
            throw new SchedulerManageException("Error resume trigger", e);
        }
    }

    @Override
    public void deleteTrigger(ObjectKey key)
    {
        log.debug("Delete trigger {}", key);
        try {
            scheduler.unscheduleJob(triggerKey(key));
        }
        catch (SchedulerException e) {
            log.error("Error delete trigger", e);
            throw new SchedulerManageException("Error delete trigger", e);
        }
    }

    @Override
    public List<JobExecutionInfo> getJobExecutionLog(ObjectKey jobKey, Integer limit)
    {
        return monitor.getJobExecutions(jobKey, limit);
    }

    private JobKey jobKey(ObjectKey key)
    {
        return JobKey.jobKey(key.getName(), key.getGroup());
    }

    private TriggerKey triggerKey(ObjectKey key)
    {
        return TriggerKey.triggerKey(key.getName(), key.getGroup());
    }

}
