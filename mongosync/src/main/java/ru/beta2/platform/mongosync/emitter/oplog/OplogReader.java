package ru.beta2.platform.mongosync.emitter.oplog;

import com.mongodb.*;
import org.bson.types.BSONTimestamp;
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
    private final MongoClient mongoClient;
    private final OplogTracker tracker;
    private final OplogHandler handler;

    private Thread thread;

    public OplogReader(EmitterConfig cfg, MongoClient mongoClient, OplogTracker tracker, OplogHandler handler)
    {
        this.cfg = cfg;
        this.mongoClient = mongoClient;
        this.tracker = tracker;
        this.handler = handler;
    }

    @Override
    public void start()
    {
        log.trace("Start OplogReader");

        thread = new Thread(new OplogMonitor(
                mongoClient.getDB(cfg.getOplogDbName()).getCollection(cfg.getOplogCollectionName())
        ), "OplogMonitor");
        thread.start();
    }

    @Override
    public void stop()
    {
        log.trace("Stop OplogReader");
        try {
            log.trace("Join OplogMonitor");
            thread.join();
        }
        catch (InterruptedException e) {
            log.debug("Thread interrupted", e);
        }
    }

    private class OplogMonitor implements Runnable
    {

        private final DBCollection oplog;

        private volatile boolean running;

        private OplogMonitor(DBCollection oplog)
        {
            this.oplog = oplog;
        }

        @Override
        public void run()
        {
            log.trace("Run OplogMonitor");
            running = true;
            BSONTimestamp lastProcessed = getInitialLastProcessed();
            log.debug("LastProcessed is '{}'", lastProcessed);
            while (running) {
                DBObject lastProcessedCondition = lastProcessed != null ? new BasicDBObject("ts", new BasicDBObject("$gt", lastProcessed)) : null;
                log.debug("Create DBCursor with lastProcessedCondition={}", lastProcessedCondition);
                DBCursor cursor = oplog.find(lastProcessedCondition).addOption(Bytes.QUERYOPTION_TAILABLE).addOption(Bytes.QUERYOPTION_AWAITDATA);
                log.trace("About to start cycle");
                while (cursor.hasNext()) {
                    log.trace("Get next oplog record");
                    DBObject obj = cursor.next();
                    try {
                        log.trace("Process next oplog record");
                        handler.processOplogRecord(obj);
                        lastProcessed = (BSONTimestamp) obj.get("ts");
                        log.debug("Oplog record processed, ts={}", lastProcessed);
                        tracker.setLastProcessed(lastProcessed);
                    }
                    catch (ProcessOplogException e) {
                        log.error("Error process oplog record", e);
                        if (cfg.isOplogRepeatReadOnError()) {
                            log.trace("Repeat oplog read after {} ms", cfg.getOplogRepeatReadOnErrorInterval());
                            try {
                                Thread.sleep(cfg.getOplogRepeatReadOnErrorInterval());
                            }
                            catch (InterruptedException e1) {
                                log.trace("Repeat wait interrupted, exit", e1);
                                running = false;
                                break;
                            }
                        }
                        else {
                            throw new RuntimeException("Error process oplog record", e);
                        }
                    }
                    log.trace("End of cycle iteration");
                }
                log.trace("Cycle is over");
            }
            log.trace("OplogMonitor exit");
        }

        private BSONTimestamp getInitialLastProcessed()
        {
            BSONTimestamp lastProcessed = tracker.getLastProcessed();
            if (lastProcessed == null) {
                log.trace("Find last oplog record");
                DBCursor cursor = oplog.find().sort(new BasicDBObject("$natural", -1)).limit(1);
                if (cursor.hasNext()) {
                    return (BSONTimestamp) cursor.next().get("ts");
                }
            }
            return lastProcessed;
        }
    }

}
