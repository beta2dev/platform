package ru.beta2.platform.core.mongo;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 19:18
 */
public class MongoConnectionConfig
{

    private final Configuration cfg;

    public MongoConnectionConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public String getHost()
    {
        return cfg.getString("host", "localhost");
    }

    public int getPort()
    {
        return cfg.getInt("port", 27017);
    }

}
