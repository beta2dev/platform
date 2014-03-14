package ru.beta2.platform.mongosync;

import org.apache.commons.configuration.Configuration;
import ru.beta2.eco.config.front.BaseConfig;
import ru.beta2.platform.mongosync.emitter.EmitterConfig;
import ru.beta2.platform.mongosync.receiver.ReceiverConfig;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 21:33
 */
public class MongoSyncConfig extends BaseConfig
{

    // todo !!! remove this from here
    private ConnectionConfig connectionConfig;
    private EmitterConfig emitterConfig;
    private ReceiverConfig receiverConfig;

    public MongoSyncConfig(Configuration cfg)
    {
        super(cfg);
        connectionConfig = new ConnectionConfig(cfg.subset("connection"));
        emitterConfig = new EmitterConfig(cfg.subset("emitter"));
        receiverConfig = new ReceiverConfig(cfg.subset("receiver"));
    }

    public ConnectionConfig getConnectionConfig()
    {
        return connectionConfig;
    }

    public EmitterConfig getEmitterConfig()
    {
        return emitterConfig;
    }

    public ReceiverConfig getReceiverConfig()
    {
        return receiverConfig;
    }
}
