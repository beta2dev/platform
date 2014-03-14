package ru.beta2.platform.core.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.parameters.BasicComponentParameter;

import java.util.Properties;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 22.02.14
 * Time: 9:59
 */
public class ConfigUtils
{
    public static Object getConfigurationKey(String configName)
    {
        return "Configuration:" + configName;
    }

    //    public static Parameter getConfigurationParameter(String configName)
//    {
//        return new BasicComponentParameter(getConfigurationKey(configName));
//    }
//
//    public static void registerConfig(MutablePicoContainer pico, Class<?> configClass, String configName)
//    {
//        pico.as(CACHE).addAdapter(new ConfigurationComponent(configName));
//        pico.as(CACHE).addComponent(configClass, ConfigUtils.getConfigurationParameter(configName));
//    }
//
//    public static void registerConfig(MutablePicoContainer pico, Class<?> configClass)
//    {
//        ConfigName configName = configClass.getAnnotation(ConfigName.class);
//        if (configName == null) {
//            throw new IllegalArgumentException("Annotation 'ConfigName' is absent for class " + configClass);
//        }
//        registerConfig(pico, configClass, configName.value());
//    }

}
