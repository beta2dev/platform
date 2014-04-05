package ru.beta2.platform.mongosync.receiver;

import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import ru.beta2.platform.core.assembly.AssemblyUnit;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.config.ConfigService;

import java.util.concurrent.Executor;

/**
 * User: inc
 * Date: 31.03.14
 * Time: 22:21
 */
public class MongoSyncReceiverUnit extends AssemblyUnit
{
    public MongoSyncReceiverUnit(PicoContainerFactory containerFactory, ConfigService configService, Executor assemblyExecutor)
    {
        super(containerFactory, configService, assemblyExecutor);
    }

    @Override
    protected String getConfigName()
    {
        return "mongosync-receiver";
    }

    @Override
    protected void populatePico(MutablePicoContainer pico) throws Exception
    {
        ReceiverConfig cfg = new ReceiverConfig(createConfiguration());

        if (!cfg.isEnabled()) {
            log.info("MongoSyncReceiver is OFF");
            return;
        }

        pico.addComponent(cfg);
        pico.as(Characteristics.CACHE).addComponent(MongoSyncReceiver.class);
    }
}
