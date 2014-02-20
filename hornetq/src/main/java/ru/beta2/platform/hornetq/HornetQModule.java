package ru.beta2.platform.hornetq;

import ru.beta2.platform.core.assembly.Module;
import ru.beta2.platform.core.assembly.ModuleContext;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 19:19
 */
public class HornetQModule implements Module
{
    @Override
    public void mount(ModuleContext ctx)
    {
        ctx.getApplicationContainer().as(CACHE).addComponent(HornetQUnit.class);
    }
}
