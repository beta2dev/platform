package ru.beta2.platform.mongosync.receiver;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 22:09
 */
public class ReceiverConfig
{

    private final Configuration cfg;

    public ReceiverConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    public String getQueue()
    {
        return cfg.getString("queue");
    }

}
