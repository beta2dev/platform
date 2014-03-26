package ru.beta2.platform.taskgate.execute;

import org.picocontainer.MutablePicoContainer;
import ru.beta2.platform.core.assembly.AssemblyUnit;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.config.ConfigService;

import java.util.concurrent.Executor;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 14:40
 */
public class TaskExecutorUnit extends AssemblyUnit
{
    public TaskExecutorUnit(PicoContainerFactory containerFactory, ConfigService configService, Executor assemblyExecutor)
    {
        super(containerFactory, configService, assemblyExecutor);
    }

    @Override
    protected String getConfigName()
    {
        return "taskgate-executor";
    }

    @Override
    protected void populatePico(MutablePicoContainer pico) throws Exception
    {
//        ExecutorConfig cfg = new ExecutorConfig(createXMLConfiguration());
        ExecutorConfig cfg = new ExecutorConfig(createConfiguration());
        if (!cfg.isEnabled()) {
            log.info("TaskExecutor is OFF");
            return;
        }

        pico.addComponent(cfg);
        pico.as(CACHE).addComponent(TaskExecutor.class);
    }
}
