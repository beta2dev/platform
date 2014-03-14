package ru.beta2.platform.taskgate;

import ru.beta2.platform.core.assembly.Module;
import ru.beta2.platform.core.assembly.ModuleContext;
import ru.beta2.platform.taskgate.allocate.TaskAllocatorUnit;
import ru.beta2.platform.taskgate.execute.TaskExecutorUnit;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 14:33
 */
public class TaskGateModule implements Module
{
    @Override
    public void mount(ModuleContext ctx)
    {
        ctx.getApplicationContainer()
                .as(CACHE).addComponent(TaskAllocatorUnit.class)
                .as(CACHE).addComponent(TaskExecutorUnit.class);
    }
}
