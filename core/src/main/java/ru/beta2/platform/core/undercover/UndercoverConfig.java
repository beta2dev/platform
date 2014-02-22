package ru.beta2.platform.core.undercover;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 17:15
 */
public class UndercoverConfig
{

    private final Configuration cfg;

    public UndercoverConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    public String getHost()
    {
        return cfg.getString("host", "127.0.0.1");
    }

    public int getPort()
    {
        return cfg.getInt("port", 8088);
    }

    public boolean isCoversListEnabled()
    {
        return cfg.getBoolean("coversListEnabled", false);
    }

    public boolean isCoverMetadataEnabled()
    {
        return cfg.getBoolean("coverMetadataEnabled", false);
    }
}
