package ru.beta2.platform.core.assembly;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.config.Config;
import ru.beta2.platform.core.config.ConfigListener;
import ru.beta2.platform.core.config.ConfigService;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.io.StringReader;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 13:21
 */
public abstract class AssemblyUnit implements Startable
{

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final PicoContainerFactory containerFactory;
    private final ConfigService configService;

    private Config config;
    private HandlerRegistration configListenerRegistration;

    private MutablePicoContainer pico;

    public AssemblyUnit(PicoContainerFactory containerFactory, ConfigService configService)
    {
        this.containerFactory = containerFactory;
        this.configService = configService;
    }

    @Override
    public synchronized void start()
    {
        log.trace("Starting assembly unit: {}", getConfigName());
        config = configService.getConfig(getConfigName());
        configListenerRegistration = config.addListener(new ConfigHandler());

        createAndStartPico();
        log.info("Assembly unit started: {}", getConfigName());
    }

    @Override
    public synchronized void stop()
    {
        log.trace("Stopping assembly unit: {}", getConfigName());
        stopPico();

        configListenerRegistration.removeHandler();
        config = null;
        log.info("Assembly unit stopped: {}", getConfigName());
    }

    protected abstract String getConfigName();

    protected abstract void populatePico(MutablePicoContainer pico);

    protected String getConfigValue()
    {
        return config.getValue();
    }

    protected Configuration createConfiguration()
    {
        PropertiesConfiguration cfg = new PropertiesConfiguration();
        cfg.setThrowExceptionOnMissing(true);
        try {
            cfg.load(new StringReader(getConfigValue()));
            return cfg;
        }
        catch (ConfigurationException e) {
            log.error("Error create configuration object", e);
            throw new RuntimeException("Error create configuration object", e);
        }
    }

    private synchronized void createAndStartPico()
    {
        log.trace("Create pico");
        pico = containerFactory.createPicoContainer();
        log.trace("Populate pico");
        populatePico(pico);
        log.trace("Start pico");
        pico.start();
    }

    private synchronized void stopPico()
    {
        log.trace("Stop pico");
        pico.stop();
        pico = null;
    }

    private class ConfigHandler implements ConfigListener
    {
        @Override
        public void onConfigChange(Config config)
        {
            synchronized (AssemblyUnit.this) {
                log.trace("Restarting assembly unit: {}", getConfigName());
                stopPico();
                createAndStartPico();
                log.info("Assembly unit restarted: {}", getConfigName());
            }
        }
    }
}
