package ru.beta2.platform.mongosync.receiver;

import com.mongodb.WriteConcern;
import org.apache.commons.configuration.Configuration;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 22:09
 */
public class ReceiverConfig
{

    private final Configuration cfg;

    private WriteConcern oplogWriteConcern;

    public ReceiverConfig(Configuration cfg)
    {
        this.cfg = cfg;
        this.oplogWriteConcern = createOplogWriteConcern();
    }

    public boolean isEnabled()
    {
        return cfg.getBoolean("enabled", false);
    }

    public String getAddress()
    {
        return cfg.getString("address");
    }

    public String getQueue()
    {
        return cfg.getString("queue");
    }

    public boolean isAutoCreateQueue()
    {
        return cfg.getBoolean("autoCreateQueue", true);
    }

    public boolean isDropExistentCollectionBeforeClone()
    {
        return cfg.getBoolean("dropExistentCollectionBeforeClone", true);
    }

    public int getReceiveTimeout()
    {
        return cfg.getInt("receiveTimeout", 1000);
    }

    public int getReceiveErrorTimeout()
    {
        return cfg.getInt("receiveErrorTimeout", 10000);
    }

    public WriteConcern getOplogWriteConcern()
    {
        return oplogWriteConcern;
    }

    private WriteConcern createOplogWriteConcern()
    {
        WriteConcern wc = WriteConcern.valueOf(cfg.getString("oplogWriteConcern", "FSYNCED"));
        return wc != null ? wc : WriteConcern.FSYNCED;
    }

}
