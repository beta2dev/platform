package ru.beta2.platform.mongosync;

import ru.beta2.platform.core.assembly.Module;
import ru.beta2.platform.core.assembly.ModuleContext;
import ru.beta2.platform.mongosync.emitter.MongoSyncEmitterUnit;
import ru.beta2.platform.mongosync.receiver.MongoSyncReceiverUnit;

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
        ctx.getApplicationContainer().addComponent(MongoSyncEmitterUnit.class);
        ctx.getApplicationContainer().addComponent(MongoSyncReceiverUnit.class);
    }
}
