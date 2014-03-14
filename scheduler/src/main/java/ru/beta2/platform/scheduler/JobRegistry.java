package ru.beta2.platform.scheduler;

import org.picocontainer.ComponentAdapter;
import org.quartz.Job;
import org.quartz.spi.JobFactory;
import ru.beta2.platform.core.util.HandlerRegistration;

/**
 * User: Inc
 * Date: 08.03.14
 * Time: 16:05
 */
public interface JobRegistry
{

    HandlerRegistration addJobImplementation(Class<? extends Job> jobClass);

    HandlerRegistration addJobComponent(ComponentAdapter<? extends Job> adapter);

    HandlerRegistration addJobFactory(Class<? extends Job> jobClass, JobFactory jobFactory);

}
