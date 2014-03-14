package ru.beta2.platform.mongosync.emitter;

import org.apache.commons.configuration.Configuration;
import ru.beta2.eco.config.front.BaseConfig;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 22:07
 */
public class EmitterConfig extends BaseConfig
{
    public EmitterConfig(Configuration cfg)
    {
        super(cfg);
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

}
