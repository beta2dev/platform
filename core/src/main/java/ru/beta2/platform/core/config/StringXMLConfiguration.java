package ru.beta2.platform.core.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.StringReader;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 16:28
 */
public class StringXMLConfiguration extends XMLConfiguration
{
    public StringXMLConfiguration(String configValue) throws ConfigurationException
    {
        setThrowExceptionOnMissing(true);
        load(new StringReader(configValue));
    }
}
