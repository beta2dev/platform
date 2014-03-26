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

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    private final static String PLATFORM_CONFIG_URL = "/platform.properties";

    private final Logger log = LoggerFactory.getLogger(PlatformBootstrap.class);
    private final DefaultPicoContainer rootContainer;

    public PlatformBootstrap()
    {
        rootContainer = new DefaultPicoContainer();
    }

    public static void main(String[] args)
    {
        new PlatformBootstrap().run();
    }

    void run()
    {
        long startAt = System.currentTimeMillis();
        log.info("Begin bootstrap, run as {}", ManagementFactory.getRuntimeMXBean().getName());

        Runtime.getRuntime().addShutdownHook(new Shutdown());
        BootstrapExecutor executor = new BootstrapExecutor();
        try {
            Configuration cfg = getPlatformConfiguration();

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
            rootContainer.addComponent(new DefaultPicoContainerFactory(rootContainer));
            rootContainer.addComponent(new ApplicationConfig(cfg.subset("app")));
            rootContainer.as(CACHE).addComponent(ApplicationUnit.class);

            // Executor
            rootContainer.addComponent(executor);

            // Start
            log.info("Starting root container");
            rootContainer.start();
            log.info("Root container started");
        }
        catch (MalformedURLException e) {
            log.error("Error accessing platform properties file", e);
            throw new RuntimeException(e);
        }
        catch (ConfigurationException e) {
            log.error("Error parsing platform properties file", e);
            throw new RuntimeException(e);
        }
        long elapsed = System.currentTimeMillis() - startAt;
        log.info("*** SERVER IS STARTED in " + elapsed + " ms ***");

        executor.runExecutionCycle();
    }

    //
    //  Implementation
    //

    private Configuration getPlatformConfiguration() throws MalformedURLException, ConfigurationException
    {
        URL props = getClass().getResource(PLATFORM_CONFIG_URL);
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
