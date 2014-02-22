package ru.beta2.platform.hornetq;

import org.apache.commons.configuration.Configuration;
import ru.beta2.platform.core.config.ConfigName;

/**
 * User: Inc
 * Date: 22.02.14
 * Time: 8:44
 */
@ConfigName("hornetq-client")
public class HornetQClientConfig
{

    private final Configuration cfg;

    public HornetQClientConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public int getReconnectAttempts()
    {
        return cfg.getInt("reconnectAttempts", -1);
    }

    public int getInitialConnectAttempts()
    {
        return cfg.getInt("initialConnectAttempts", -1);
    }
}
