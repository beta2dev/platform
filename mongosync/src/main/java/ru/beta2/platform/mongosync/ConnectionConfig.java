package ru.beta2.platform.mongosync;

import org.apache.commons.configuration.Configuration;
import ru.beta2.eco.config.front.BaseConfig;

/**
 * todo !!! remove this from here
 * User: Inc
 * Date: 17.02.14
 * Time: 22:23
 */
public class ConnectionConfig extends BaseConfig
{
    public ConnectionConfig(Configuration cfg)
    {
        super(cfg);
    }

    public String getHost()
    {
        return cfg.getString("host", "127.0.0.1");
    }

    public boolean hasAuth()
    {
        return getUsername() != null;
    }

    public String getUsername()
    {
        return cfg.getString("username", null);
    }

    public String getPassword()
    {
        return cfg.getString("password", null);
    }

    public String getAuthDB()
    {
        return cfg.getString("authDB", "admin");
    }
}
