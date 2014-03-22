package ru.beta2.platform.scheduler;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.references.ThreadLocalReference;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 14:49
 */
public class PlatformJobFactory implements JobFactory, JobRegistry
{

    private final Logger log = LoggerFactory.getLogger(PlatformJobFactory.class);

    private final MutablePicoContainer container;

    private final ThreadLocalAdapter<TriggerFiredBundle> triggerFiredBundleAdapter = new ThreadLocalAdapter<TriggerFiredBundle>(TriggerFiredBundle.class);
    private final ThreadLocalAdapter<JobDetail> jobDetailAdapter = new ThreadLocalAdapter<JobDetail>(JobDetail.class);
    private final ThreadLocalAdapter<Scheduler> schedulerAdapter = new ThreadLocalAdapter<Scheduler>(Scheduler.class);

    public PlatformJobFactory(PicoContainerFactory containerFactory)
    {
        this.container = containerFactory.createPicoContainer();

        this.container.addAdapter(triggerFiredBundleAdapter);
        this.container.addAdapter(jobDetailAdapter);
        this.container.addAdapter(schedulerAdapter);
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException
    {
        log.debug("Create new job: key={}, class={}", bundle.getJobDetail().getKey(), bundle.getJobDetail().getJobClass());
        try {
            triggerFiredBundleAdapter.setValue(bundle);
            jobDetailAdapter.setValue(bundle.getJobDetail());
            schedulerAdapter.setValue(scheduler);

            Object obj = container.getComponent(bundle.getJobDetail().getJobClass());

            if (obj instanceof JobFactory) {
                return ((JobFactory) obj).newJob(bundle, scheduler);
            }

            return (Job) obj;
        }
        finally {
            triggerFiredBundleAdapter.removeValue();
            jobDetailAdapter.removeValue();
            schedulerAdapter.removeValue();
        }
    }

    public List<Class<? extends Job>> getRegisteredJobTypes()
    {
        List<ComponentAdapter<Job>> adapters = container.getComponentAdapters(Job.class);
        List<Class<? extends Job>> found = new ArrayList<Class<? extends Job>>();
        for (ComponentAdapter<Job> a : adapters) {
            found.add(a.getComponentImplementation());
        }
        return found;
    }

    @Override
    public HandlerRegistration addJobImplementation(final Class<? extends Job> jobClass)
    {
        container.addComponent(jobClass);
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                container.removeComponent(jobClass);
            }
        };
    }

    @Override
    public HandlerRegistration addJobComponent(final ComponentAdapter<? extends Job> adapter)
    {
        container.addAdapter(adapter);
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                container.removeComponent(adapter.getComponentKey());
            }
        };
    }

    @Override
    public HandlerRegistration addJobFactory(final Class<? extends Job> jobClass, final JobFactory jobFactory)
    {
        return addJobComponent(new AbstractAdapter<Job>(jobClass, jobClass)
        {
            @Override
            public Job getComponentInstance(PicoContainer picoContainer, Type type) throws PicoCompositionException
            {
                try {
                    return jobFactory.newJob(
                            picoContainer.getComponent(TriggerFiredBundle.class),
                            picoContainer.getComponent(Scheduler.class)
                    );
                }
                catch (SchedulerException e) {
                    log.error("Error create new job through factory");
                    throw new PicoCompositionException("Error create new job through factory", e);
                }
            }

            @Override
            public void verify(PicoContainer picoContainer) throws PicoCompositionException
            {
            }

            @Override
            public String getDescriptor()
            {
                return "JobFactoryAdapter";
            }
        });
    }

    private static class ThreadLocalAdapter<T> extends AbstractAdapter<T>
    {

        private final ThreadLocalReference<T> reference = new ThreadLocalReference<T>();

        public ThreadLocalAdapter(Class componentInterface)
        {
            super(componentInterface, componentInterface);
        }

        @Override
        public T getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
        {
            return reference.get();
        }

        @Override
        public void verify(PicoContainer container) throws PicoCompositionException
        {
        }

        @Override
        public String getDescriptor()
        {
            return "ThreadLocalAdapter";
        }

        public void setValue(T value)
        {
            reference.set(value);
        }

        public void removeValue()
        {
            reference.remove();
        }
    }
}
