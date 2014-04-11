package ru.beta2.platform.mongosync;

import ru.beta2.platform.core.assembly.Module;
import ru.beta2.platform.core.assembly.ModuleContext;
import ru.beta2.platform.mongosync.emitter.MongoSyncEmitterUnit;
import ru.beta2.platform.mongosync.receiver.MongoSyncReceiverUnit;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: inc
 * Date: 31.03.14
 * Time: 22:17
 */
public class MongoSyncModule implements Module
{
    @Override
    public void mount(ModuleContext ctx)
    {
        ctx.getApplicationContainer().as(CACHE).addComponent(MongoSyncEmitterUnit.class);
        ctx.getApplicationContainer().as(CACHE).addComponent(MongoSyncReceiverUnit.class);
    }
}
