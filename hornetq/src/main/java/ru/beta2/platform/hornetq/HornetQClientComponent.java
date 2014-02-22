package ru.beta2.platform.hornetq;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.picocontainer.injectors.AbstractInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * User: Inc
 * Date: 21.02.14
 * Time: 21:51
 */
public class HornetQClientComponent extends AbstractAdapter<ServerLocator> implements LifecycleStrategy
{

    private final Logger log = LoggerFactory.getLogger(HornetQClientComponent.class);

    public HornetQClientComponent()
    {
        super(ServerLocator.class, ServerLocator.class); // будем считать, что нам не известна реализация
    }

    @Override
    public ServerLocator getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException
    {
        HornetQClientConfig cfg = container.getComponent(HornetQClientConfig.class);

        if (cfg == null) {
            throw configRequired();
        }

        // с помощью определяем порядок инстанциирования (то есть чтобы ServerLocator создавалась и стартовало после сервера)
        container.getComponent(HornetQServerUnit.class);

        log.trace("Create ServerLocator");
        ServerLocator serverLocator = HornetQClient.createServerLocatorWithoutHA(new TransportConfiguration(InVMConnectorFactory.class.getName()));
        serverLocator.setReconnectAttempts(cfg.getReconnectAttempts());
        serverLocator.setInitialConnectAttempts(cfg.getInitialConnectAttempts());
        return serverLocator;
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException
    {
        if (container.getComponentAdapter(HornetQClientConfig.class) == null) {
            throw configRequired();
        }
    }

    @Override
    public String getDescriptor()
    {
        return "HornetQClientComponent";
    }

    private PicoCompositionException configRequired()
    {
        log.error("HornetQClientComponent requires HornetQClientConfig");
        return new AbstractInjector.UnsatisfiableDependenciesException("HornetQClientComponent requires HornetQClientConfig");
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
        log.trace("Close ServerLocator");
        ((ServerLocator) component).close();
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
