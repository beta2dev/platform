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

    public String getConfigDB()
    {
        return cfg.getString("configDB", "platform");
    }

    public String getConfigCollection()
    {
        return cfg.getString("configCollection", "config");
    }
}
