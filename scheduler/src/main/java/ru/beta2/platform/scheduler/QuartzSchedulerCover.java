package ru.beta2.platform.scheduler;

import org.picocontainer.Startable;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.undercover.UndercoverService;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.ArrayList;
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

    // todo !!! ??? add logging ?

    private final UndercoverService undercoverService;
    private final Scheduler scheduler;
    private final PlatformJobFactory jobFactory;

    private HandlerRegistration undercoverRegistration;

    public QuartzSchedulerCover(UndercoverService undercoverService, Scheduler scheduler, PlatformJobFactory jobFactory)
    {
        this.undercoverService = undercoverService;
        this.scheduler = scheduler;
        this.jobFactory = jobFactory;
    }

    @Override
    public void start()
    {
        undercoverRegistration = undercoverService.registerCover("/scheduler", SchedulerCover.class, this);
    }

    @Override
    public void stop()
    {
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
    public Set<JobKey> getJobKeys()
    {
        try {
            return scheduler.getJobKeys(GroupMatcher.anyJobGroup());
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e); // todo !!! handle
        }
    }

    @Override
    public JobDescriptor getJob(JobKey key)
    {
        try {
            return new JobDescriptor(scheduler.getJobDetail(key));
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e); // todo !!! handle
        }
    }

    @Override
    public void addJob(JobDescriptor job)
    {
        JobDetail jobDetail = JobBuilder
                .newJob(getJobClass(job.getType()))
                .withIdentity(job.getName(), job.getGroup())
                .withDescription(job.getDescription())
                .setJobData(new JobDataMap(job.getData()))
                .storeDurably()
                .build();

        try {
            scheduler.addJob(jobDetail, false);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e); // todo !!! handle
        }
    }

    @Override
    public void updateJob(JobKey key, JobDescriptor job)
    {
        // todo !!! implement
    }

    private Class<? extends Job> getJobClass(String type)
    {
        try {
            return (Class<? extends Job>) Class.forName(type); // todo !!! maybe check for class is job child
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e); // todo !!! handle
        }
    }

}
