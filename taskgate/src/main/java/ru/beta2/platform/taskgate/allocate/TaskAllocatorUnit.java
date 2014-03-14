package ru.beta2.platform.taskgate.allocate;

import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.assembly.AssemblyUnit;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.config.ConfigService;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 14:39
 */
public class TaskAllocatorUnit extends AssemblyUnit
{

    private final Logger log = LoggerFactory.getLogger(TaskAllocatorUnit.class);

    public TaskAllocatorUnit(PicoContainerFactory containerFactory, ConfigService configService)
    {
        super(containerFactory, configService);
    }

    @Override
    protected String getConfigName()
    {
        return "taskgate-allocator";
    }

    @Override
    protected void populatePico(MutablePicoContainer pico) throws Exception
    {
        AllocatorConfig cfg = new AllocatorConfig(createConfiguration());
        if (!cfg.isEnabled()) {
            log.info("TaskAllocator is OFF");
            return;
        }

        pico.addComponent(cfg);

    }
}
