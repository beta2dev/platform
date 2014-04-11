package ru.beta2.platform.core.config;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 19.02.14
 * Time: 23:39
 */
public class ConfigServerConfig
{

    private final Configuration cfg;

    public ConfigServerConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public String getConfigDbName()
    {
        return cfg.getString("configDbName", "platform");
    }

    public String getConfigCollectionName()
    {
        return cfg.getString("configCollectionName", "config");
    }
}
