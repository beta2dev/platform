package ru.beta2.platform.taskgate;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.*;

/**
 * User: Inc
 * Date: 11.03.14
 * Time: 17:36
 */
public abstract class TargetsContainConfig<T>
{

    private static final String TARGEY_KEY = "target";
    private static final int TARGEY_KEY_LENGTH = TARGEY_KEY.length();

    private final HierarchicalConfiguration hcfg;
    protected final Configuration cfg;

    private List<T> targets = new ArrayList<T>();
    private Map<String, T> targetsMap = new HashMap<String, T>();

    protected TargetsContainConfig(HierarchicalConfiguration hcfg)
    {
        this(hcfg, hcfg);
    }

    protected TargetsContainConfig(Configuration cfg)
    {
        this(null, cfg);
    }

    private TargetsContainConfig(HierarchicalConfiguration hcfg, Configuration cfg)
    {
        this.hcfg = hcfg;
        this.cfg = cfg;
        buildTargets();
    }

    public List<T> getTargets()
    {
        return targets;
    }

    public T getTarget(String name)
    {
        return targetsMap.get(name);
    }

    protected abstract T createTargetConfig(Configuration cfg, String defaultName);

    protected abstract String getTargetName(T target);

    private void buildTargets()
    {
        if (hcfg != null) {
            for (HierarchicalConfiguration cfg0 : hcfg.configurationsAt("targets.target")) {
                addTarget(createTargetConfig(cfg0, null));
            }
        }
        else {
            HashSet<String> targetNames = new HashSet<String>();

            for (Iterator<String> i = cfg.getKeys("target"); i.hasNext(); ) {
                String key = i.next();
                int dotindex = key.indexOf('.', TARGEY_KEY_LENGTH + 1);
                String targetName = key.substring(TARGEY_KEY_LENGTH + 1, dotindex != -1 ? dotindex : key.length());
                if (!targetNames.contains(targetName)) {
                    targetNames.add(targetName);
                    addTarget(createTargetConfig(cfg.subset(targetName), targetName));
                }
            }
        }
    }

    private void addTarget(T target)
    {
        targets.add(target);
        targetsMap.put(getTargetName(target), target);
    }

}
