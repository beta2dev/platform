package ru.beta2.platform.core.assembly;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.config.*;
import ru.beta2.platform.core.util.HandlerRegistration;

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
        String assemblyName = getAssemblyName();

        log.trace("Starting assembly unit: {}", assemblyName);
        config = configService.getConfig(getConfigName());
        configListenerRegistration = config.addListener(new ConfigHandler());

        createAndStartPico();
        log.info("Assembly unit started: {}", assemblyName);
    }

    @Override
    public synchronized void stop()
    {
        String assemblyName = getAssemblyName();

        log.trace("Stopping assembly unit: {}", assemblyName);
        stopAndDisposePico();

        configListenerRegistration.removeHandler();
        config = null;
        log.info("Assembly unit stopped: {}", assemblyName);
    }

    protected String getAssemblyName()
    {
        return getConfigName();
    }

    protected abstract String getConfigName();

    protected abstract void populatePico(MutablePicoContainer pico) throws Exception;

    protected String getConfigValue()
    {
        return config.getValue();
    }

    protected Configuration createConfiguration()
    {
        try {
            return new StringPropertiesConfiguration(getConfigValue());
        }
        catch (ConfigurationException e) {
            log.error("Error create configuration object", e);
            throw new RuntimeException("Error create configuration object", e);
        }
    }

    protected HierarchicalConfiguration createXMLConfiguration()
    {
        try {
            return new StringXMLConfiguration(getConfigValue());
        }
        catch (ConfigurationException e) {
            log.error("Error create XML configuration object", e);
            throw new RuntimeException("Error create XML configuration object", e);
        }
    }

    private synchronized void createAndStartPico()
    {
        String assemblyName = getAssemblyName();

        log.trace("Create pico: {}", assemblyName);
        pico = containerFactory.createPicoContainer();
        log.trace("Populate pico: {}", assemblyName);
        try {
            populatePico(pico);
        }
        catch (Exception e) {
            log.error("Error populating pico: " + assemblyName, e);
            throw new RuntimeException("Error populating pico", e);
        }
        log.trace("Start pico: {}", assemblyName);
        pico.start();
    }

    private synchronized void stopAndDisposePico()
    {
        String assemblyName = getAssemblyName();

        log.trace("Stop pico: {}", assemblyName);
        pico.stop();
        log.trace("Dispose pico: {}", assemblyName);
        pico.dispose();
        pico = null;
    }

    private class ConfigHandler implements ConfigListener
    {
        @Override
        public void onConfigChange(Config config)
        {
            synchronized (AssemblyUnit.this) {
                String assemblyName = getAssemblyName();

                log.info("Restarting assembly unit: {}", assemblyName);
                stopAndDisposePico();
                log.trace("Assembly unit ready to restart: {}", assemblyName);
                createAndStartPico();
                log.info("Assembly unit restarted: {}", assemblyName);
            }
        }
    }
}
