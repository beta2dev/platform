package ru.beta2.platform.mongosync.emitter;

import org.bson.BasicBSONObject;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.util.HandlerRegistration;
import ru.beta2.platform.mongosync.protocol.CloneCollectionsMessage;
import ru.beta2.platform.mongosync.protocol.OplogRecordMessage;

import java.util.*;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 0:40
 */
public class MongoSyncEmitter implements Startable, OplogReader.Handler
{

    private final Logger log = LoggerFactory.getLogger(MongoSyncEmitter.class);
    // todo !!! add more logging

    private final EmitterConfig cfg;
    private final EmitManager emitManager;
    private final MessageTransmitter transmitter;

    private final Object emitLock = new Object();
    private final Map<String, Set<String>> namespaceRoutes = new HashMap<String, Set<String>>();

    private HandlerRegistration emitReg;

    public MongoSyncEmitter(EmitterConfig cfg, EmitManager emitManager, MessageTransmitter transmitter)
    {
        this.cfg = cfg;
        this.emitManager = emitManager;
        this.transmitter = transmitter;
    }

    @Override
    public void start()
    {
        log.trace("Starting MongoSyncEmitter");

        synchronized (emitLock) {
            emitReg = emitManager.addListener(new EmitSyncListener());
            for (EmitInfo emit : emitManager.getEmits()) {
                addRoutes(emit.getAddress(), emit.getNamespaces());
            }
        }

        log.info("MongoSyncEmitter started.");
    }

    @Override
    public void stop()
    {
        log.trace("Stopping MongoSyncEmitter");

        emitReg.removeHandler();
        emitReg = null;

        log.info("MongoSyncEmitter stopped.");
    }

    @Override
    public void processOplogRecord(BasicBSONObject record)
    {
        // todo !!! we heed smart handling send error
        // todo !!! handle binary data
        if ("n".equals(record.getString("op"))) {
            return;
        }
        String ns = record.getString("ns");
        synchronized (emitLock) {
            Set<String> routes = getNamespaceRoutes(ns);
            if (routes.isEmpty()) {
                return;
            }
            transmitter.sendMessage(routes, new OplogRecordMessage(record));
        }
    }

    private Set<String> getNamespaceRoutes(String ns)
    {
        Set<String> routes = namespaceRoutes.get(ns);
        return routes != null ? routes : Collections.<String>emptySet();
    }

    private void addRoutes(String address, Set<String> namespaces)
    {
        for (String ns : namespaces) {
            Set<String> routes = namespaceRoutes.get(ns);
            if (routes == null) {
                routes = new HashSet<String>();
                namespaceRoutes.put(ns, routes);
            }
            routes.add(address);
        }
    }

    private void removeRoutes(String address, Set<String> namespaces)
    {
        for (String ns : namespaces) {
            Set<String> routes = namespaceRoutes.get(ns);
            if (routes == null) {
                continue;
            }
            routes.remove(address);
        }
    }

    private class EmitSyncListener implements EmitListener
    {
        @Override
        public void onEmitStart(String address, Set<String> namespaces)
        {
            synchronized (emitLock) {
                transmitter.sendMessage(address,
                        new CloneCollectionsMessage(cfg.getCloneCollectionHostname(), namespaces));
                addRoutes(address, namespaces);
            }
        }

        @Override
        public void onEmitStop(String address, Set<String> namespaces)
        {
            synchronized (emitLock) {
                removeRoutes(address, namespaces);
            }
        }
    }
}
