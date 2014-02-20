package ru.beta2.platform.core.assembly;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 14:22
 */
public class ApplicationConfig
{

    private final Configuration cfg;

    public ApplicationConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public String getConfigName()
    {
        return cfg.getString("configName", "app");
    }
}
