package ru.beta2.platform.scheduler;

import org.apache.commons.configuration.Configuration;
import ru.beta2.platform.core.mongo.MongoConstants;

/**
 * User: inc
 * Date: 22.03.14
 * Time: 14:30
 */
public class JobExecutionMonitorConfig
{

    private final Configuration cfg;

    public JobExecutionMonitorConfig(Configuration cfg)
    {
        this.cfg = cfg;
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    public String getDbName()
    {
        return cfg.getString("dbName", MongoConstants.DEFAULT_DB_NAME);
    }

    public String getCollectionName()
    {
        return cfg.getString("collectionName", "scheduler.joblog");
    }
}
