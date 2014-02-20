package ru.beta2.platform.hornetq;

import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.impl.HornetQServerImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.parameters.ComponentParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 19:31
 */
public class HornetQServerComponent extends ConstructorInjector<HornetQServer> implements LifecycleStrategy
{

    private final Logger log = LoggerFactory.getLogger(HornetQServerComponent.class);

    public HornetQServerComponent()
    {
        super(HornetQServer.class, HornetQServerImpl.class, ComponentParameter.DEFAULT);
    }

    @Override
    public String getDescriptor()
    {
        return "HornetQServer Component";
    }

    //
    //  LifecycleStrategy
    //

    @Override
    public void start(Object component)
    {
        log.trace("Starting HornetQServer");
        try {
            ((HornetQServer) component).start();
            log.info("HornetQServer started");
        }
        catch (Exception e) {
            log.error("Error starting HornetQServer", e);
            throw new HornetQLifecycleException("HornetQServer start failed", e);
        }
    }

    @Override
    public void stop(Object component)
    {
        log.trace("Stopping HornetQServer");
        try {
            ((HornetQServer) component).stop();
            log.info("HornetQServer stopped");
        }
        catch (Exception e) {
            log.error("Error stopping HornetQServer", e);
            throw new HornetQLifecycleException("HornetQServer stop failed", e);
        }
    }

    @Override
    public void dispose(Object component)
    {
        // noop
    }

    @Override
    public boolean hasLifecycle(Class<?> type)
    {
        return true;
    }

    @Override
    public boolean isLazy(ComponentAdapter<?> adapter)
    {
        System.out.println("*** IS LAZILY ***");
        return false;
    }
}
