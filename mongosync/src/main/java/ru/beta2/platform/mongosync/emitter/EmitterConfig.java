package ru.beta2.platform.mongosync.emitter;

import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 22:07
 */
public class EmitterConfig
{

    private final Configuration cfg;

    public EmitterConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    public String getEmitsDbName()
    {
        return cfg.getString("emitsDbName", "local");
    }

    public String getEmitsCollectionName()
    {
        return cfg.getString("emitsCollectionName", "mongosync.emitter.emits");
    }

    public String getOplogTrackerDbName()
    {
        return cfg.getString("oplogTrackerDbName", "local");
    }

    public String getOplogTrackerCollectionName()
    {
        return cfg.getString("oplogTrackerCollectionName", "mongosync.emitter.tracks");
    }

    public String getOplogTrackerKey()
    {
        return cfg.getString("oplogTrackerKey", "defaultTracker");
    }

    public String getCloneCollectionHostname()
    {
        return cfg.getString("cloneCollectionHostname");
    }

    public String getOplogDbName()
    {
        return cfg.getString("oplogDbName", "local");
    }

    public String getOplogCollectionName()
    {
        return cfg.getString("oplogCollectionName", "oplog.$main");
    }

    public boolean isOplogContinueOnProcessError()
    {
        return cfg.getBoolean("oplogContinueOnProcessError", true);
    }

    public int getOplogErrorTimeout()
    {
        return cfg.getInt("oplogErrorTimeout", 1000);
    }

}
