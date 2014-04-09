package ru.beta2.platform.mongosync.emitter.oplog;

import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.mongosync.emitter.EmitterConfig;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:44
 */
public class OplogReader implements Startable
{

    private final Logger log = LoggerFactory.getLogger(OplogReader.class);

    private final EmitterConfig cfg;

    private final OplogHandler handler;

    public OplogReader(EmitterConfig cfg, OplogHandler handler)
    {
        this.cfg = cfg;
        this.handler = handler;
    }

    @Override
    public void start()
    {
        // todo !!! implement
    }

    @Override
    public void stop()
    {
        // todo !!! implement
    }

}
