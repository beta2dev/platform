package ru.beta2.platform.taskgate.execute;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import ru.beta2.platform.taskgate.TargetsContainConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 14:53
 */
public class ExecutorConfig extends TargetsContainConfig<TargetConfig> implements ProcessingConfig
{

    private volatile List<Integer> ackStatuses;

    public ExecutorConfig(HierarchicalConfiguration hcfg)
    {
        super(hcfg);
    }

    public ExecutorConfig(Configuration cfg)
    {
        super(cfg);
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    @Override
    public int getConnectTimeout()
    {
        return cfg.getInt("connectTimeout", 10000);
    }

    @Override
    public int getSocketTimeout()
    {
        return cfg.getInt("socketTimeout", 60000);
    }

    @Override
    public boolean isAckOnException()
    {
        return cfg.getBoolean("ackOnException", false);
    }

    @Override
    public boolean isAckOnHttpStatus(int status)
    {
        return getAckStatuses().contains(status);
    }

    public List<Integer> getAckStatuses()
    {
        if (ackStatuses == null) {
            synchronized (this) {
                if (ackStatuses == null) {
                    ackStatuses = buildAckStatuses(cfg, null);
                }
            }
        }
        return ackStatuses;
    }

    public static List<Integer> buildAckStatuses(Configuration cfg, List<Integer> defaults)
    {
        List<Object> list = cfg.getList("ackOnHttpStatus");

        if (list.isEmpty()) {
            return defaults != null ? defaults : Collections.singletonList(200);
        }

        List<Integer> ackStatuses = new ArrayList<Integer>();
        for (Object o : list) {
            ackStatuses.add(Integer.parseInt(o.toString()));
        }
        return ackStatuses;
    }

    @Override
    protected TargetConfig createTargetConfig(Configuration cfg, String defaultName)
    {
        return new TargetConfig(cfg, this, defaultName);
    }

    @Override
    protected String getTargetName(TargetConfig target)
    {
        return target.getName();
    }

}
