package ru.beta2.platform.taskgate.allocate;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import ru.beta2.platform.taskgate.TargetsContainConfig;

/**
 * User: Inc
 * Date: 09.03.14
 * Time: 14:43
 */
public class AllocatorConfig extends TargetsContainConfig<TargetConfig>
{

    private TargetConfig defaultTarget;

    public AllocatorConfig(HierarchicalConfiguration hcfg)
    {
        super(hcfg);
        initDefaultTarget();
    }

    public AllocatorConfig(Configuration cfg)
    {
        super(cfg);
        initDefaultTarget();
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    public TargetConfig getDefaultTarget()
    {
        return defaultTarget;
    }

    @Override
    protected TargetConfig createTargetConfig(Configuration cfg, String defaultName)
    {
        return new TargetConfig(cfg, defaultName);
    }

    @Override
    protected String getTargetName(TargetConfig target)
    {
        return target.getName();
    }

    private void initDefaultTarget()
    {
        for (TargetConfig t : getTargets()) {
            if (t.isDefault()) {
                defaultTarget = t;
                break;
            }
        }
    }
}
