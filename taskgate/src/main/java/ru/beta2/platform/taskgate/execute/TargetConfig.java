package ru.beta2.platform.taskgate.execute;

import org.apache.commons.configuration.Configuration;

import java.util.List;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 15:23
 */
public class TargetConfig implements ProcessingConfig
{

    private final Configuration cfg;
    private final String defaultName;
    private final ExecutorConfig ecfg;

    private volatile List<Integer> ackStatuses;

    public TargetConfig(Configuration cfg, ExecutorConfig ecfg)
    {
        this(cfg, ecfg, null);
    }

    public TargetConfig(Configuration cfg, ExecutorConfig ecfg, String defaultName)
    {
        this.cfg = cfg;
        this.ecfg = ecfg;
        this.defaultName = defaultName;
    }

    public String getName()
    {
        return defaultName != null ? cfg.getString("name", defaultName) : cfg.getString("name");
    }

    public boolean isDisabled()
    {
        return cfg.getBoolean("disabled", false);
    }

    public String getQueue()
    {
        return cfg.getString("queue");
    }

    public String getTaskBaseURL()
    {
        return cfg.getString("taskBaseURL");
    }

    @Override
    public int getConnectTimeout()
    {
        return cfg.getInt("connectTimeout", ecfg.getConnectTimeout());
    }

    @Override
    public int getSocketTimeout()
    {
        return cfg.getInt("socketTimeout", ecfg.getSocketTimeout());
    }

    @Override
    public boolean isAckOnException()
    {
        return cfg.getBoolean("ackOnException", ecfg.isAckOnException());
    }

    @Override
    public boolean isAckOnHttpStatus(int status)
    {
        if (ackStatuses == null) {
            synchronized (this) {
                if (ackStatuses == null) {
                    ackStatuses = ExecutorConfig.buildAckStatuses(cfg, ecfg.getAckStatuses());
                }
            }
        }
        return ackStatuses.contains(status);
    }

}
