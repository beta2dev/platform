package ru.beta2.platform.mongosync.emitter;

import org.bson.BasicBSONObject;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:44
 */
public class OplogReader implements Startable
{

    public interface Handler
    {
        void processOplogRecord(BasicBSONObject record);
    }

    private final Logger log = LoggerFactory.getLogger(OplogReader.class);

    private final EmitterConfig cfg;

    private final Handler handler;

    public OplogReader(EmitterConfig cfg, Handler handler)
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
