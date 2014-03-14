package ru.beta2.platform.mongosync.receiver;

import org.apache.commons.configuration.Configuration;
import ru.beta2.eco.config.front.BaseConfig;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 22:09
 */
public class ReceiverConfig extends BaseConfig
{
    public ReceiverConfig(Configuration cfg)
    {
        super(cfg);
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

}
