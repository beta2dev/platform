package ru.beta2.platform.mongosync.emitter;

import org.apache.commons.configuration.Configuration;
import ru.beta2.platform.core.mongo.MongoConstants;

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

    public String getRoutesDbName()
    {
        return cfg.getString("dbName", MongoConstants.DEFAULT_DB_NAME);
    }

    public String getRoutesCollectionName()
    {
        return cfg.getString("collectionName", "mongosync.emitter.routes");
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

}
