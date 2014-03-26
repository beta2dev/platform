package ru.beta2.platform.scheduler;

import org.picocontainer.MutablePicoContainer;
import ru.beta2.platform.core.assembly.AssemblyUnit;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.config.ConfigService;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 04.03.14
 * Time: 12:05
 */
public class QuartzSchedulerUnit extends AssemblyUnit
{

    public QuartzSchedulerUnit(PicoContainerFactory containerFactory, ConfigService configService, Executor assemblyExecutor)
    {
        super(containerFactory, configService, assemblyExecutor);
    }

    @Override
    protected String getConfigName()
    {
        return "quartz-scheduler";
    }

    @Override
    protected void populatePico(MutablePicoContainer pico) throws Exception
    {
        SchedulerConfig cfg = new SchedulerConfig(createConfiguration().subset("scheduler"));

        pico.addComponent(createConfigProperties());
        pico.addComponent(cfg);
        pico.as(CACHE).addAdapter(new QuartzSchedulerComponent());
        pico.as(CACHE).addComponent(QuartzSchedulerCover.class);

        pico.addComponent(cfg.getJobExecutionMonitorConfig());
        pico.as(CACHE).addComponent(JobExecutionMonitor.class);
    }

    private Properties createConfigProperties() throws IOException
    {
        Properties props = new Properties();
        props.load(new StringReader(getConfigValue()));
        return props;
    }
}
