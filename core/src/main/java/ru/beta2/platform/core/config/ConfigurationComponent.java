package ru.beta2.platform.core.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * User: Inc
 * Date: 22.02.14
 * Time: 11:23
 */
public class ConfigurationComponent extends AbstractAdapter<Configuration>
{

    private final Logger log = LoggerFactory.getLogger(ConfigurationComponent.class);

    public ConfigurationComponent(String configName)
    {
        super(ConfigUtils.getConfigurationKey(configName), Configuration.class);
    }

    @Override
    public Configuration getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
    {
        ConfigServer srv = container.getComponent(ConfigServer.class);

        if (srv == null) {
            throw configServerRequired();
        }

        String configName = getComponentKey().toString();
        try {
            log.trace("Create Configuration, name=" + configName);
            return new StringPropertiesConfiguration(srv.getConfigValue(configName));
        }
        catch (ConfigurationException e) {
            log.error("Error creating Configuration, name=" + configName, e);
            throw new PicoCompositionException("Error creating config, name=" + configName, e);
        }
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException
    {
        if (container.getComponentAdapter(ConfigServer.class) == null) {
            throw configServerRequired();
        }
    }

    @Override
    public String getDescriptor()
    {
        return "ConfigurationComponent";
    }

    private PicoCompositionException configServerRequired()
    {
        log.error("ConfigurationComponent requires ConfigServer");
        return new AbstractInjector.UnsatisfiableDependenciesException("ConfigurationComponent requires ConfigServer");
    }
}
