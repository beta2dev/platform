package ru.beta2.platform.core.mongo;

import com.mongodb.MongoClient;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.UnknownHostException;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 18:40
 */
public class MongoComponent extends AbstractAdapter<MongoClient> implements LifecycleStrategy
{

    private final Logger log = LoggerFactory.getLogger(MongoComponent.class);

    public MongoComponent()
    {
        super(MongoClient.class, MongoClient.class);
    }

    @Override
    public MongoClient getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
    {
        MongoConnectionConfig cfg = container.getComponent(MongoConnectionConfig.class);
        if (cfg == null) {
            throw configRequired();
        }
        log.trace("Create MongoClient");
        try {
            return new MongoClient(cfg.getHost(), cfg.getPort());
        }
        catch (UnknownHostException e) {
            log.error("Error connecting MongoDB", e);
            throw new PicoCompositionException("Error connecting MongoDB", e);
        }
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException
    {
        if (container.getComponentAdapter(MongoConnectionConfig.class) == null) {
            throw configRequired();
        }
    }

    @Override
    public String getDescriptor()
    {
        return "MongoComponent";
    }

    private PicoCompositionException configRequired()
    {
        log.error("MongoComponent requires MongoConnectionConfig");
        return new AbstractInjector.UnsatisfiableDependenciesException("MongoComponent requires MongoConnectionConfig");
    }

    //
    //  LifecycleStrategy
    //

    @Override
    public void start(Object component)
    {
        // noop
    }

    @Override
    public void stop(Object component)
    {
        // noop
    }

    @Override
    public void dispose(Object component)
    {
        log.trace("Close MongoClient");
        ((MongoClient) component).close();
    }

    @Override
    public boolean hasLifecycle(Class<?> type)
    {
        return true;
    }

    @Override
    public boolean isLazy(ComponentAdapter<?> adapter)
    {
        return false;
    }
}
