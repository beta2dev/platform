package ru.beta2.platform.core.mongo;

import com.mongodb.MongoClient;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;

import java.lang.reflect.Type;
import java.net.UnknownHostException;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 18:40
 */
public class MongoComponent extends AbstractAdapter<MongoClient>
{
    public MongoComponent()
    {
        super(MongoClient.class, MongoClient.class);
    }

    @Override
    public MongoClient getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
    {
        MongoConnectionConfig cfg = container.getComponent(MongoConnectionConfig.class);
        if (cfg == null) {
            throw new AbstractInjector.UnsatisfiableDependenciesException("MongoComponent is required MongoConnectionConfiguration");
        }

//        MongoClient mongo = null;
        try {
            return new MongoClient(cfg.getHost(), cfg.getPort());
//            mongo = new MongoClient(cfg.getHost(), cfg.getPort());
//            if (cfg.hasAuth()) {
//                DB authDB = mongo.getDB(cfg.getAuthDB());
//                if (!authDB.authenticate(cfg.getUsername(), cfg.getPassword())) {
//                    throw new PicoCompositionException("MongoDB authentication failed");
//                }
//            }
//            return mongo;
        }
        catch (UnknownHostException e) {
            throw new PicoCompositionException("Error connecting MongoDB", e);
        }
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException
    {
        if (container.getComponentAdapter(MongoConnectionConfig.class) == null) {
            throw new AbstractInjector.UnsatisfiableDependenciesException("MongoComponent is required MongoConnectionConfiguration");
        }
    }

    @Override
    public String getDescriptor()
    {
        return "MongoDB Component";
    }
}
