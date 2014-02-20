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

//    @Override
//    public boolean hasAuth()
//    {
//        return getUsername() != null;
//    }
//
//    @Override
//    public String getUsername()
//    {
//        return cfg.getString("username", null);
//    }
//
//    @Override
//    public char[] getPassword()
//    {
//        String psswd = cfg.getString("password", null);
//        return psswd == null ? null : psswd.toCharArray();
//    }
//
//    @Override
//    public String getAuthDB()
//    {
//        return cfg.getString("authDB", "admin");
//    }
}
