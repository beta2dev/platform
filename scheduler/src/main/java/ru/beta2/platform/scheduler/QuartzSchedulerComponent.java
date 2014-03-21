package ru.beta2.platform.scheduler;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;

import java.lang.reflect.Type;
import java.util.Properties;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 13:16
 */
public class QuartzSchedulerComponent extends AbstractAdapter<Scheduler> implements LifecycleStrategy
{

    private final Logger log = LoggerFactory.getLogger(QuartzSchedulerComponent.class);

    public QuartzSchedulerComponent()
    {
        super(Scheduler.class, Scheduler.class);
    }

    @Override
    public Scheduler getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
    {
        Properties props = container.getComponent(Properties.class);
        if (props == null) {
            throw configRequired();
        }

        try {
            log.trace("Get Scheduler from SchedulerFactory");
            Scheduler scheduler =  new StdSchedulerFactory(props).getScheduler();

            JobFactory jobFactory = container.getComponent(JobFactory.class);
            if (jobFactory != null) {
                log.trace("Assign JobFactory to Scheduler");
                scheduler.setJobFactory(jobFactory);
            }

            return scheduler;
        }
        catch (SchedulerException e) {
            log.error("Error creating QuartzScheduler", e);
            throw new PicoCompositionException("Error creating QuartzScheduler", e);
        }
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException
    {
        if (container.getComponentAdapter(Properties.class) == null) {
            throw configRequired();
        }
    }

    @Override
    public String getDescriptor()
    {
        return "QuartzSchedulerComponent";
    }

    //
    //  LifecycleStrategy
    //

    @Override
    public void start(Object component)
    {
        log.trace("Starting QuartzScheduler");
        try {
            ((Scheduler) component).start();
            log.info("QuartzScheduler started");
        }
        catch (SchedulerException e) {
            log.error("Error starting QuartzScheduler", e);
            throw new LifecycleException("Error starting QuartzScheduler", e);
        }
    }

    @Override
    public void stop(Object component)
    {
        long st = System.currentTimeMillis();
        log.trace("Shutdown QuartzScheduler, waiting for jobs to complete...");
        try {
            ((Scheduler) component).shutdown(true); // выполняем shutdown в stop(), так как не предполагаем перезапуска scheduler'a без его переинстанциирования
            log.info("QuartzScheduler shutdown complete in {} ms", System.currentTimeMillis() - st);
        }
        catch (SchedulerException e) {
            log.error("Error shutdown QuartzScheduler", e);
            throw new RuntimeException("Error shutdown QuartzScheduler", e);
        }
    }

    @Override
    public void dispose(Object component)
    {
    }

    @Override
    public boolean hasLifecycle(Class<?> type)
    {
        return true;
    }

    @Override
    public boolean isLazy(ComponentAdapter<?> adapter)
    {
        return false;
    }

    //
    //  Details
    //

    private PicoCompositionException configRequired()
    {
        log.error("QuartzSchedulerComponent requires Properties with configuration");
        return new AbstractInjector.UnsatisfiableDependenciesException("QuartzSchedulerComponent requires Properties with configuration");
    }

}
