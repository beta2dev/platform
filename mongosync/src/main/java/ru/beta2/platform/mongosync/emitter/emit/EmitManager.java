package ru.beta2.platform.mongosync.emitter.emit;

import com.mongodb.MongoClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.undercover.CoverRegistrable;
import ru.beta2.platform.core.util.HandlerRegistration;
import ru.beta2.platform.mongosync.emitter.EmitterConfig;

import java.util.Collection;
import java.util.Set;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:15
 */
public class EmitManager implements EmitManagerCover, CoverRegistrable
{

    private final Logger log = LoggerFactory.getLogger(EmitManager.class);

    private final MongoCollection emits;

    private EmitLifecycleHandler lifecycleHandler;

    public EmitManager(EmitterConfig cfg, MongoClient mongo)
    {
        emits = new Jongo(mongo.getDB(cfg.getEmitsDbName())).getCollection(cfg.getEmitsCollectionName());
    }

    public HandlerRegistration setLifecycleHandler(EmitLifecycleHandler handler)
    {
        log.trace("Set lifecycle listener");
        if (lifecycleHandler != null) {
            throw new IllegalStateException("EmitListener already assigned");
        }
        this.lifecycleHandler = lifecycleHandler;
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                EmitManager.this.lifecycleHandler = null;
            }
        };
    }

    @Override
    public Collection<EmitInfo> getEmits()
    {
        log.trace("Get emits");
        return IteratorUtils.toList(emits.find().as(EmitInfo.class).iterator());
    }

    @Override
    public void startEmit(String address, Collection<String> namespaces)
    {
        log.debug("Start emit: address={}, namespaces={}", address, namespaces);
        EmitRecord r = getEmitRecord(address);
        if (r == null) {
            log.trace("Create new emit record");
            r = new EmitRecord();
        }

        Set<String> added = r.addNamespaces(namespaces);
        log.debug("Actual added namespaces: address={}, added={}", address, added);
        if (added.isEmpty()) {
            log.trace("There is no new namespaces added for address '{}', do nothing", address);
            return; // ===>
        }
        else {
            log.trace("Process to EmitLifecycleHandler.onEmitStart()");
            try {
                lifecycleHandler.onEmitStart(address, added);
            }
            catch (EmitLifecycleException e) {
                log.error("Error start emit in handler", e);
                throw new EmitException("Error start emit in handler", e);
            }
        }

        log.trace("Save EmitRecord");
        save(r);
    }

    @Override
    public void stopEmitAll(String address)
    {
        log.debug("Stop emit all: address={}", address);
        EmitRecord r = getEmitRecord(address);
        if (r == null) {
            log.debug("EmitRecord not found for address '{}', do nothing", address);
            return; // ===>
        }
        if (r.hasNamespaces()) {
            log.trace("Process to EmitLifecycleHandler.onEmitStop()");
            lifecycleHandler.onEmitStop(address, r.getNamespaces());
        }

        log.trace("Remove EmitRecord");
        emits.remove(r.getId());
    }

    @Override
    public void stopEmit(String address, Collection<String> namespaces)
    {
        log.debug("Stop emit: address={}, namespaces={}", address, namespaces);
        EmitRecord r = getEmitRecord(address);
        if (r == null) {
            log.debug("EmitRecord not found for address '{}', do nothing", address);
            return; // ===>
        }

        Set<String> removed = r.removeNamespaces(namespaces);
        log.debug("Actual removed namespaces: address={}, removed={}", address, removed);
        if (removed.isEmpty()) {
            log.trace("There is no namespaces removed for address '{}', do nothing", address);
            return; // ===>
        }
        else {
            log.trace("Process to EmitLifecycleHandler.onEmitStop()");
            lifecycleHandler.onEmitStop(address, removed);
        }

        log.trace("Save EmitRecord");
        save(r);
    }

    @Override
    public void changeEmit(String address, Collection<String> namespaces)
    {
        log.debug("Change emit: address={}, namespaces={}", address, namespaces);
        EmitRecord r = getEmitRecord(address);
        EmitInfo.ChangeResult changeResult;
        if (r == null) {
            log.debug("EmitRecord not found for address '{}', create new empty EmitRecord", address);
            r = new EmitRecord();
        }
        changeResult = r.changeNamespaces(namespaces);
        if (CollectionUtils.isNotEmpty(changeResult.getAdded())) {
            log.trace("Start added emits");
            startEmit(address, changeResult.getAdded());
        }
        if (CollectionUtils.isNotEmpty(changeResult.getRemoved())) {
            log.trace("Stop removed emits");
            stopEmit(address, changeResult.getRemoved());
        }
    }

    @Override
    public String getCoverPath()
    {
        return "/mongosync-emitter-emitmanager";
    }

    @Override
    public Class getCoverInterface()
    {
        return EmitManagerCover.class;
    }

    //
    //  Implementation details
    //

    private EmitRecord getEmitRecord(String address)
    {
        return emits.findOne("{address: #}", address).as(EmitRecord.class);
    }

    private void save(EmitRecord emit)
    {
        emits.save(emit);
    }
}

class EmitRecord extends EmitInfo
{
    @Id
    private ObjectId id;

    public ObjectId getId()
    {
        return id;
    }

    public void setId(ObjectId id)
    {
        this.id = id;
    }
}
