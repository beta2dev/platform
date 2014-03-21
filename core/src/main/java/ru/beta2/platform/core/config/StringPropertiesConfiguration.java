package ru.beta2.platform.core.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.StringReader;

/**
 * User: Inc
 * Date: 22.02.14
 * Time: 11:29
 */
public class StringPropertiesConfiguration extends PropertiesConfiguration
{
    public StringPropertiesConfiguration(String configValue) throws ConfigurationException
    {
        setThrowExceptionOnMissing(true);
        if (configValue != null) {
            load(new StringReader(configValue));
        }
    }
}
