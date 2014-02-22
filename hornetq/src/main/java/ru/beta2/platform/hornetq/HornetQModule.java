package ru.beta2.platform.hornetq;

import org.picocontainer.MutablePicoContainer;
import ru.beta2.platform.core.assembly.Module;
import ru.beta2.platform.core.assembly.ModuleContext;
import ru.beta2.platform.core.config.ConfigComponent;

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
        MutablePicoContainer pico = ctx.getApplicationContainer();

        // Server
        pico.as(CACHE).addComponent(HornetQServerUnit.class);

        // Client
        pico.as(CACHE).addAdapter(new ConfigComponent(HornetQClientConfig.class));
        pico.as(CACHE).addAdapter(new HornetQClientComponent());
    }
}
