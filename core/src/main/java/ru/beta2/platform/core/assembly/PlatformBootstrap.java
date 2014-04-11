package ru.beta2.platform.core.assembly;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.config.ConfigServer;
import ru.beta2.platform.core.config.ConfigServerConfig;
import ru.beta2.platform.core.mongo.MongoComponent;
import ru.beta2.platform.core.mongo.MongoConnectionConfig;
import ru.beta2.platform.core.undercover.UndercoverConfig;
import ru.beta2.platform.core.undercover.UndercoverServer;
import ru.beta2.platform.core.util.LifecycleException;

import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 12:00
 */
public class PlatformBootstrap
{

    // todo DEFFERED реализовать независимое от ошибок pico ComponentMonitor (да и вообще управление запуском/перезапуском)
    // (то есть нужно бы сделать, чтобы ошибка при старте модуля/юнита не приводила к остановке старта сервера)

    private final static String DEFAULT_PLATFORM_CONFIG_URL = "platform.properties";

    private final Logger log = LoggerFactory.getLogger(PlatformBootstrap.class);
    private final Configuration cfg;
    private final DefaultPicoContainer rootContainer;

    public PlatformBootstrap(Configuration cfg)
    {
        this.cfg = cfg;
        rootContainer = new DefaultPicoContainer();
    }

    public static void main(String[] args)
    {
        try {
            new PlatformBootstrap(getPlatformConfiguration(
                    args != null && args.length > 0 ? args[0] : DEFAULT_PLATFORM_CONFIG_URL
            )).run();
        }
        catch (MalformedURLException e) {
            throw new LifecycleException("Error accessing platform properties file", e);
        }
        catch (ConfigurationException e) {
            throw new LifecycleException("Error parsing platform properties file", e);
        }
    }

    void run()
    {
        long startAt = System.currentTimeMillis();
        log.info("Begin bootstrap, run as {}", ManagementFactory.getRuntimeMXBean().getName());

        Runtime.getRuntime().addShutdownHook(new Shutdown());
        BootstrapExecutor executor = new BootstrapExecutor();

        setupComponents(executor);

        // Start
        log.info("Starting root container");
        rootContainer.start();

        log.info("Root container started");
        long elapsed = System.currentTimeMillis() - startAt;
        log.info("*** SERVER IS STARTED in " + elapsed + " ms ***");

        executor.runExecutionCycle();
    }

    //
    //  Implementation
    //

    private void setupComponents(BootstrapExecutor executor)
    {
        // MongoDB
        rootContainer.addComponent(new MongoConnectionConfig(cfg.subset("mongo")));
        rootContainer.as(CACHE).addAdapter(new MongoComponent());

        // ConfigServer
        rootContainer.addComponent(new ConfigServerConfig(cfg.subset("config")));
        rootContainer.as(CACHE).addComponent(ConfigServer.class);

        // Undercover
        rootContainer.addComponent(new UndercoverConfig(cfg.subset("undercover")));
        rootContainer.as(CACHE).addComponent(UndercoverServer.class);

        // Application AssemblyUnit
        ApplicationConfig appConfig = new ApplicationConfig(cfg.subset("app"));
        if (appConfig.isEnabled()) {
            rootContainer.addComponent(new DefaultPicoContainerFactory(rootContainer));
            rootContainer.addComponent(appConfig);
            rootContainer.as(CACHE).addComponent(ApplicationUnit.class);
        }
        else {
            log.info("Application is DISABLED");
        }

        // Executor
        rootContainer.addComponent(executor);
    }

    private static Configuration getPlatformConfiguration(String configResource) throws MalformedURLException, ConfigurationException
    {
        URL props = PlatformBootstrap.class.getResource("/" + configResource);
        return props != null ? new PropertiesConfiguration(props) : new BaseConfiguration();
    }

    private class Shutdown extends Thread
    {
        private Shutdown()
        {
            super("PlatformShutdownHook");
        }
        @Override
        public void run()
        {
            log.info("Begin shutdown");
            LifecycleState state = rootContainer.getLifecycleState();
            if (state.isStarted()) {
                log.info("Stopping root container");
                rootContainer.stop();
            }
            log.info("Disposing root container");
            rootContainer.dispose();
            log.info("Shutdown done");
        }
    }

    private class BootstrapExecutor implements Executor
    {

        private final BlockingQueue<Runnable> commands = new LinkedBlockingQueue<Runnable>();

        @Override
        public void execute(Runnable command)
        {
            log.trace("Offer command to queue");
            commands.offer(command);
        }

        public void runExecutionCycle()
        {
            log.trace("Enter execution cycle");
            while (true) {
                try {
                    log.trace("Take next command");
                    Runnable r = commands.take();
                    log.trace("Run command");
                    r.run();
                }
                catch (InterruptedException e) {
                    log.trace("Interrupted");
                    break;
                }
            }
            log.trace("Leave execution cycle");
        }

    }

}
