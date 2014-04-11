package ru.beta2.platform.mongosync.receiver;

import com.mongodb.*;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.*;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.LifecycleException;
import ru.beta2.platform.hornetq.util.SingleSessionHelper;
import ru.beta2.platform.mongosync.protocol.CloneCollectionsMessage;
import ru.beta2.platform.mongosync.protocol.OplogRecordMessage;
import ru.beta2.platform.mongosync.protocol.ProtocolException;
import ru.beta2.platform.mongosync.protocol.ProtocolMessage;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 0:40
 */
public class MongoSyncReceiver implements Startable
{

    private final Logger log = LoggerFactory.getLogger(MongoSyncReceiver.class);

    private final ReceiverConfig cfg;
    private final MongoClient mongo;
    private final SingleSessionHelper sessionHelper;

    private ClientConsumer consumer;
    private MessageProcessor processor;
    private Thread processorThread;

    public MongoSyncReceiver(ReceiverConfig cfg, MongoClient mongo, ServerLocator serverLocator)
    {
        this.cfg = cfg;
        this.mongo = mongo;
        this.sessionHelper = new SingleSessionHelper(log, serverLocator) {
            @Override
            protected ClientSession createSession(ClientSessionFactory sessionFactory) throws HornetQException
            {
                return sessionFactory.createTransactedSession();
            }
        };
    }

    @Override
    public void start()
    {
        log.trace("Starting MongoSyncReceiver");

        sessionHelper.create();

        log.debug("Create message consumer, queue={}", cfg.getQueue());
        try {
            checkQueueReady();
            consumer = getSession().createConsumer(cfg.getQueue());
        }
        catch (HornetQException e) {
            log.error("Error create message consumer", e);
            throw new LifecycleException("Error create message consumer", e);
        }

        processor = new MessageProcessor();
        processorThread = new Thread(processor, "MessageProcessor");
        log.trace("Start MessageProcessor thread");
        processorThread.start();

        sessionHelper.start();

        log.info("MongoSyncReceiver started");
    }

    @Override
    public void stop()
    {
        log.trace("Stopping MongoSyncReceiver");

        processor.stop();
        log.trace("Join MessageProcessor thread");
        try {
            processorThread.join();
        }
        catch (InterruptedException e) {
            log.trace("Interrupted while waiting for MessageProcessor thread join");
        }

        log.trace("Close message consumer");
        try {
            consumer.close();
        }
        catch (HornetQException e) {
            log.warn("Error close message consumer", e);
        }

        sessionHelper.stopAndClose();

        log.info("MongoSyncReceiver stopped");
    }

    private ClientSession getSession()
    {
        return sessionHelper.getSession();
    }

    private void checkQueueReady() throws HornetQException
    {
        ClientSession.QueueQuery qq = getSession().queueQuery(SimpleString.toSimpleString(cfg.getQueue()));
        if (!qq.isExists() && cfg.isAutoCreateQueue()) {
            log.debug("Create durable queue '{}' bound to address '{}'", cfg.getQueue(), cfg.getAddress());
            getSession().createQueue(cfg.getAddress(), cfg.getQueue(), true);
        }
    }

    private boolean processClientMessage(ClientMessage message)
    {
        log.debug("Message received: {}", message);

        ProtocolMessage pmsg;
        try {
            pmsg = ProtocolMessage.createProtocolMessage(message);
        }
        catch (HornetQException e) {
            log.error("Error create protocol message, rollback", e);
            return rollback(); // ===>
        }
        catch (ProtocolException e) {
            log.warn("Message protocol error, commit (skip this message)", e);
            return commit(); // ===>
        }

        try {
            processProtocolMessage(pmsg);
        }
        catch (RuntimeException e) {
            log.error("Error processing protocol message, rollback", e);
            return rollback(); // ===>
        }

        try {
            log.trace("Acknowledge message");
            message.acknowledge();
        }
        catch (HornetQException e) {
            stopConsumeOnError("Acknowledge", e);
            return rollback();
        }

        return commit();
    }

    private void processProtocolMessage(ProtocolMessage pmsg)
    {
        log.debug("Process protocol message: {}", pmsg);
        if (pmsg instanceof OplogRecordMessage) {
            processOplogRecord((OplogRecordMessage) pmsg);
        }
        else if (pmsg instanceof CloneCollectionsMessage) {
            processCloneCollections((CloneCollectionsMessage) pmsg);
        }
        else {
            log.warn("Unknown message type: {}", pmsg);
        }
    }

    private void processOplogRecord(OplogRecordMessage msg)
    {
        log.debug("Process oplog record: {}", msg);
        BSONObject obj = msg.getOplogRecord();
        String op = (String) obj.get("op");
        Namespace ns = new Namespace((String) obj.get("ns"));
        DBCollection coll = mongo.getDB(ns.db).getCollection(ns.collection);
        if ("u".equals(op)) {
            log.debug("Update: db={}, collection={}, oplog={}", ns.db, ns.collection, obj);
            coll.update(
                    new BasicDBObject((BasicBSONObject) obj.get("o2")),
                    new BasicDBObject((BasicBSONObject) obj.get("o")),
                    false, false, cfg.getOplogWriteConcern());
        }
        else if ("i".equals(op)) {
            log.debug("Insert: db={}, collection={}, oplog={}", ns.db, ns.collection, obj);
            DBObject o = new BasicDBObject((BasicBSONObject) obj.get("o"));
            coll.update(new BasicDBObject("_id", o.get("_id")), o, true, false, cfg.getOplogWriteConcern());
        }
        else if ("d".equals(op)) {
            log.debug("Delete: db={}, collection={}, oplog={}", ns.db, ns.collection, obj);
            coll.remove(new BasicDBObject((BasicBSONObject) obj.get("o")));
        }
        else {
            log.warn("Unknown db operation: {}", obj);
        }
    }

    private void processCloneCollections(CloneCollectionsMessage msg)
    {
        log.debug("Process clone collections: {}", msg);
        DB db = mongo.getDB("local");
        String from = msg.getHostname();

        for (String ns : msg.getCollections()) {
            log.debug("Clone collection '{}' from '{}'", ns, from);

            checkNamespaceClear(ns);

            BasicDBObject cmd = new BasicDBObject();
            cmd.put("cloneCollection", ns);
            cmd.put("from", from);
            CommandResult result = db.command(cmd);
            log.debug("Clone result: {}", result);
            if (result.ok()) {
                log.trace("Clone result is ok, continue");
            }
            else {
                log.error("Clone '{}' from '{}' failed: {}", ns, from, result.getErrorMessage());
                rollback();
                stopConsumeOnError("CloneCollections");
                return; // ===>
            }
        }
    }

    private void checkNamespaceClear(String namespace)
    {
        Namespace ns = new Namespace(namespace);
        DB db = mongo.getDB(ns.db);
        if (db.collectionExists(ns.collection)) {
            log.debug("Drop existent collection '{}'", namespace);
            db.getCollection(ns.collection).drop();
        }
    }

    private boolean commit()
    {
        log.trace("Commit");
        try {
            getSession().commit();
        }
        catch (HornetQException e) {
            stopConsumeOnError("Commit", e);
            return false;
        }
        return true;
    }

    private boolean rollback()
    {
        log.trace("Rollback");
        try {
            getSession().rollback();
        }
        catch (HornetQException e) {
            stopConsumeOnError("Rollback", e);
        }
        return false;
    }

    private void stopConsumeOnError(String failedAction, HornetQException e)
    {
        log.error("Stop consume messages because of HornetQ error during '" + failedAction + "'", e);
        processor.stop();
    }

    private void stopConsumeOnError(String failedAction)
    {
        log.error("Stop consume messages because of error during '" + failedAction + "'");
        processor.stop();
    }

    private class MessageProcessor implements Runnable
    {

        private volatile boolean running;

        @Override
        public void run()
        {
            log.trace("Run MessageProcessor");
            running = true;
            while (running) {
                ClientMessage msg;
                log.trace("Receive message with consumer");
                try {
                    msg = consumer.receive(cfg.getReceiveTimeout());
                }
                catch (HornetQException e) {
                    stopConsumeOnError("Receive", e);
                    break; // ===>
                }

                if (msg == null) {
                    log.trace("There is no message, continue");
                    continue;
                }

                if (!processClientMessage(msg)) {
                    log.debug("Failed to process client message, wait pause: {}", msg);
                    try {
                        Thread.sleep(cfg.getReceiveErrorTimeout());
                    }
                    catch (InterruptedException e) {
                        log.trace("MessageProcessor thread interrupted, exit");
                        break; // ===>
                    }
                }
            }

            log.trace("MessageProcessor finished");
        }

        void stop()
        {
            log.trace("MessageProcessor running off");
            running = false;
        }
    }

    private class Namespace
    {
        String db;
        String collection;

        private Namespace(String ns)
        {
            int idx = ns.indexOf('.');
            if (idx == -1) {
                log.error("Invalid namespace: '{}'", ns);
                throw new IllegalArgumentException("Invalid namespace: '" + ns + "'");
            }
            db = ns.substring(0, idx);
            collection = ns.substring(idx + 1);
        }
    }

}
