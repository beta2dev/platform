package ru.beta2.platform.scheduler;

import ru.beta2.platform.core.assembly.Module;
import ru.beta2.platform.core.assembly.ModuleContext;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 13:27
 */
public class QuartzSchedulerModule implements Module
{
    @Override
    public void mount(ModuleContext ctx)
    {
        ctx.getApplicationContainer().as(CACHE).addComponent(QuartzSchedulerUnit.class);
        ctx.getApplicationContainer().as(CACHE).addComponent(PlatformJobFactory.class);
    }
}
