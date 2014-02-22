package ru.beta2.platform.core.config;

import com.mongodb.*;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.undercover.UndercoverService;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.*;

/**
 * User: Inc
 * Date: 19.02.14
 * Time: 23:32
 */
public class ConfigServer implements ConfigService, Startable
{

    private final Logger log = LoggerFactory.getLogger(ConfigServer.class);

    private final ConfigServerConfig cfg;
    private final MongoClient mongo;
    private final UndercoverService undercover;

    private HandlerRegistration undercoverRegistration;

    private DBCollection configCollection;

    private final Map<String, ConfigImpl> loaded = Collections.synchronizedMap(new HashMap<String, ConfigImpl>());

    public ConfigServer(ConfigServerConfig cfg, MongoClient mongo, UndercoverService undercover)
    {
        this.cfg = cfg;
        this.mongo = mongo;
        this.undercover = undercover;
    }

    @Override
    public ConfigImpl getConfig(String name)
    {
        log.trace("Get config: {}", name);
        synchronized (loaded) {
            ConfigImpl c = loaded.get(name);
            if (c == null) {
                c = loadConfig(name);
                loaded.put(name, c);
            }
            return c;
        }
    }

    @Override
    public List<String> getConfigNames()
    {
        DBCursor c = configCollection.find(new BasicDBObject(), new BasicDBObject("name", 1));
        List<String> res = new ArrayList<String>();
        for (DBObject o : c) {
            res.add(String.valueOf(o.get("name")));
        }
        return res;
    }

    @Override
    public String getConfigValue(String name)
    {
        Config c = getConfig(name);
        return c != null ? c.getValue() : null;
    }

    @Override
    public void setConfigValue(String name, String value)
    {
        log.info("Set config: {}", name);
        storeConfigValue(name, value);
        getConfig(name).setValue(value);
    }

    @Override
    public void start()
    {
        log.trace("Starting ConfigServer");
        configCollection = mongo.getDB(cfg.getConfigDB()).getCollection(cfg.getConfigCollection());
        configCollection.ensureIndex("name");
        undercoverRegistration = undercover.registerCover("/config", ConfigServiceCover.class, this);
        log.info("ConfigServer started");
    }

    @Override
    public void stop()
    {
        undercoverRegistration.removeHandler();
        undercoverRegistration = null;
    }

    //
    //  Implementation
    //

    private ConfigImpl loadConfig(String name)
    {
        log.debug("Load config: {}", name);
        DBObject o = configCollection.findOne(new BasicDBObject("name", name));
        return new ConfigImpl(name, o == null ? "" : o.get("value").toString());
    }

    private void storeConfigValue(String name, String value)
    {
        log.debug("Store config value: name={}, value={}", name, value);
        BasicDBObject query = new BasicDBObject("name", name);
        BasicDBObject obj = new BasicDBObject("name", name);
        obj.put("value", value);
        WriteResult wr = configCollection.update(query, obj, true, false, WriteConcern.FSYNCED);
        CommandResult cr = wr.getLastError();
        if (!cr.ok()) {
            log.error("Error update config value:\nname={}\nerror={}\nvalue={}", name, cr.getErrorMessage(), value);
            throw new RuntimeException("Error update config value");
        }
    }

    class ConfigImpl implements Config
    {

        private final String name;
        private String value;

        private final List<ConfigListener> listeners = Collections.synchronizedList(new ArrayList<ConfigListener>());

        ConfigImpl(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;

            synchronized (listeners) {
                log.trace("Notify listeners about config change: {}", name);
                for (ConfigListener listener : listeners) {
                    listener.onConfigChange(this);
                }
            }
        }

        @Override
        public HandlerRegistration addListener(final ConfigListener listener)
        {
            listeners.add(listener);
            return new HandlerRegistration()
            {
                @Override
                public void removeHandler()
                {
                    log.trace("Remove listener for config: {}", name);
                    listeners.remove(listener);
                }
            };
        }
    }

}

