package ru.beta2.platform.taskgate.allocate;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 11.03.14
 * Time: 17:25
 */
public class TargetConfig
{

    private final Configuration cfg;
    private final String defaultName;

    public TargetConfig(Configuration cfg, String defaultName)
    {
        this.cfg = cfg;
        this.defaultName = defaultName;
    }

    public String getName()
    {
        return cfg.getString("name", defaultName);
    }

    public String getAddress()
    {
        return cfg.getString("address");
    }

    public boolean isDefault()
    {
        return cfg.getBoolean("default", false);
    }

    @Override
    public String toString()
    {
        return "TargetConfig{" +
                "name=" + getName() +
                ", address=" + getAddress() +
                ", default=" + isDefault() +
                '}';
    }
}
