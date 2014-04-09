package ru.beta2.platform.mongosync.emitter.oplog;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.mongosync.emitter.EmitterConfig;

/**
 * User: Inc
 * Date: 09.04.2014
 * Time: 15:36
 */
public class OplogTracker
{

    private final Logger log = LoggerFactory.getLogger(OplogTracker.class);

    private final EmitterConfig cfg;
    private final DBCollection tracker;

    public OplogTracker(EmitterConfig cfg, MongoClient mongo)
    {
        this.cfg = cfg;
        this.tracker = mongo.getDB(cfg.getOplogTrackerDbName()).getCollection(cfg.getOplogTrackerCollectionName());
    }

    BSONTimestamp getLastProcessed()
    {
        log.trace("Get last processed");
        DBObject obj = findTrackObject();
        return obj != null ? (BSONTimestamp) obj.get("ts") : null;
    }

    void setLastProcessed(BSONTimestamp timestamp)
    {
        log.debug("Update last processed: key={}, ts={}", cfg.getOplogTrackerKey(), timestamp);
        DBObject obj = findTrackObject();
        if (obj == null) {
            log.trace("Create new track object");
            obj = new BasicDBObject();
            obj.put("key", cfg.getOplogTrackerKey());
        }
        obj.put("ts", timestamp);
        tracker.save(obj);
    }

    private DBObject findTrackObject()
    {
        return tracker.findOne(new BasicDBObject("key", cfg.getOplogTrackerKey()));
    }

}
