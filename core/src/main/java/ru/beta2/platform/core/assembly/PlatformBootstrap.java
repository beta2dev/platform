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

import java.net.MalformedURLException;
import java.net.URL;

import static org.picocontainer.Characteristics.CACHE;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 12:00
 */
public class PlatformBootstrap
{

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
        log.info("Begin bootstrap");
        Runtime.getRuntime().addShutdownHook(new Shutdown());
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

}
