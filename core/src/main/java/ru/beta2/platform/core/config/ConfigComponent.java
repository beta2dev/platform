package ru.beta2.platform.core.config;

import org.apache.commons.configuration.ConfigurationException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.picocontainer.injectors.ConstructorInjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * User: Inc
 * Date: 22.02.14
 * Time: 12:00
 */
public class ConfigComponent extends AbstractAdapter
{

    private final Logger log = LoggerFactory.getLogger(ConfigComponent.class);

    public ConfigComponent(Class configClass)
    {
        super(configClass, configClass);
    }

    @Override
    public Object getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
    {
        String configName = getConfigName();
        if (configName == null) {
            throw configNameRequired();
        }

        ConfigServer srv = container.getComponent(ConfigServer.class);

        if (srv == null) {
            throw configServerRequired();
        }

        DefaultPicoContainer tmp = new DefaultPicoContainer(new ConstructorInjection(), container);
        try {
            log.trace("Create Configuration, name=" + configName);
            tmp.addComponent(new StringPropertiesConfiguration(srv.getConfigValue(configName)));
        }
        catch (ConfigurationException e) {
            log.error("Error creating Configuration, name=" + configName, e);
            throw new PicoCompositionException("Error creating config, name=" + configName, e);
        }

        tmp.addComponent(getComponentKey(), getComponentImplementation());

        //noinspection unchecked
        return tmp.getComponent(getComponentKey());
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException
    {
        if (getConfigName() == null) {
            throw configNameRequired();
        }

        if (container.getComponentAdapter(ConfigServer.class) == null) {
            throw configServerRequired();
        }
    }

    @Override
    public String getDescriptor()
    {
        return "ConfigComponent";
    }

    private PicoCompositionException configServerRequired()
    {
        log.error("ConfigComponent requires ConfigServer");
        return new AbstractInjector.UnsatisfiableDependenciesException("ConfigComponent requires ConfigServer");
    }

    private PicoCompositionException configNameRequired()
    {
        log.error("ConfigComponent requires config name with ConfigName annotation, configClass={}", getComponentImplementation());
        return new AbstractInjector.UnsatisfiableDependenciesException("ConfigComponent requires config name with ConfigName annotation, configClass=" + getComponentImplementation());
    }

    private String getConfigName()
    {
        @SuppressWarnings("unchecked")
        ConfigName configName = (ConfigName) getComponentImplementation().getAnnotation(ConfigName.class);
        return configName != null ? configName.value() : null;
    }

}
