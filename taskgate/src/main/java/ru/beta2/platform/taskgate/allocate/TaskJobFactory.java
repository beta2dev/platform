package ru.beta2.platform.taskgate.allocate;

import org.picocontainer.Startable;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.HandlerRegistration;
import ru.beta2.platform.scheduler.JobRegistry;

/**
 * User: inc
 * Date: 19.03.14
 * Time: 23:55
 */
public class TaskJobFactory implements JobFactory, Startable
{

    private final Logger log = LoggerFactory.getLogger(TaskJobFactory.class);

    private final JobRegistry jobRegistry;

    private HandlerRegistration jobFactoryRegistration;

    public TaskJobFactory(JobRegistry jobRegistry)
    {
        this.jobRegistry = jobRegistry;
    }

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException
    {
        // todo !!! implement
        return new TaskJob(null, null);
    }

    @Override
    public void start()
    {
        log.trace("Register TaskJob factory");
        jobFactoryRegistration = jobRegistry.addJobFactory(TaskJob.class, this);
    }

    @Override
    public void stop()
    {
        log.trace("Unregister TaskJob factory");
        jobFactoryRegistration.removeHandler();
        jobFactoryRegistration = null;
    }
}
