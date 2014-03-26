package ru.beta2.platform.core.assembly;

import org.apache.commons.configuration.Configuration;
import org.picocontainer.MutablePicoContainer;
import ru.beta2.platform.core.config.ConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 14:17
 */
public class ApplicationUnit extends AssemblyUnit
{

    private final ApplicationConfig cfg;

    public ApplicationUnit(PicoContainerFactory containerFactory, ConfigService configService, ApplicationConfig cfg, Executor assemblyExecutor)
    {
        super(containerFactory, configService, assemblyExecutor);
        this.cfg = cfg;
    }

    @Override
    protected String getConfigName()
    {
        return cfg.getConfigName();
    }

    @Override
    protected void populatePico(final MutablePicoContainer pico)
    {
        pico.addComponent(new DefaultPicoContainerFactory(pico));

        List<Class> modules;
        try {
            modules = getModulesClasses();
        }
        catch (ClassNotFoundException e) {
            log.error("Module class not found", e);
            throw new RuntimeException("Module class not found", e);
        }

        final ModuleContext ctx = new ModuleContext()
        {
            @Override
            public MutablePicoContainer getApplicationContainer()
            {
                return pico;
            }
        };

        log.trace("Mount modules");
        try {
            for (Class cl : modules) {
                Module module = (Module) cl.newInstance();
                log.debug("Mount module: {}", String.valueOf(cl));
                module.mount(ctx);
            }
        }
        catch (ReflectiveOperationException e) {
            log.error("Error create module instance", e);
            throw new RuntimeException("Error create module instance", e);
        }
    }

    private List<Class> getModulesClasses() throws ClassNotFoundException
    {
        Configuration cfg = createConfiguration();
        ArrayList<Class> classes = new ArrayList<Class>();
        for (Object o : cfg.getList("modules")) {
            classes.add(Class.forName(o.toString()));
        }
        return classes;
    }
}
