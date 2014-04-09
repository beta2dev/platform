package ru.beta2.platform.mongosync.emitter;

import org.picocontainer.MutablePicoContainer;
import ru.beta2.platform.core.assembly.AssemblyUnit;
import ru.beta2.platform.core.assembly.PicoContainerFactory;
import ru.beta2.platform.core.config.ConfigService;
import ru.beta2.platform.core.undercover.CoverRegistrator;
import ru.beta2.platform.mongosync.emitter.emit.EmitManager;
import ru.beta2.platform.mongosync.emitter.oplog.OplogReader;
import ru.beta2.platform.mongosync.emitter.oplog.OplogTracker;

import java.util.concurrent.Executor;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: inc
 * Date: 31.03.14
 * Time: 22:18
 */
public class MongoSyncEmitterUnit extends AssemblyUnit
{
    public MongoSyncEmitterUnit(PicoContainerFactory containerFactory, ConfigService configService, Executor assemblyExecutor)
    {
        super(containerFactory, configService, assemblyExecutor);
    }

    @Override
    protected String getConfigName()
    {
        return "mongosync-emitter";
    }

    @Override
    protected void populatePico(MutablePicoContainer pico) throws Exception
    {
        EmitterConfig cfg = new EmitterConfig(createConfiguration());

        if (!cfg.isEnabled()) {
            log.info("MongoSyncEmitter is OFF");
            return;
        }

        pico.addComponent(cfg);
        pico.as(CACHE).addComponent(CoverRegistrator.class);
        pico.as(CACHE).addComponent(MongoSyncEmitter.class);
        pico.as(CACHE).addComponent(EmitManager.class);
        pico.as(CACHE).addComponent(OplogReader.class);
        pico.as(CACHE).addComponent(OplogTracker.class);
        pico.as(CACHE).addComponent(SingleSessionMessageTransmitter.class);
    }
}
