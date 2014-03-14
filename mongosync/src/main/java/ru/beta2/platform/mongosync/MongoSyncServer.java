package ru.beta2.platform.mongosync;

import com.mongodb.MongoClient;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.mongosync.emitter.EmitterConfig;
import ru.beta2.platform.mongosync.receiver.ReceiverConfig;

/**
 * User: Inc
 * Date: 17.02.14
 * Time: 21:29
 */
public class MongoSyncServer implements Startable
{

    private final Logger log = LoggerFactory.getLogger(MongoSyncServer.class);
    // todo !!! add logging

    private DefaultPicoContainer pico;
    private MongoClient mongo;

    public MongoSyncServer(MongoClient mongo)
    {
        this.mongo = mongo;
    }

    // todo !!! todo нужно вынести управление сервисами (start, stop, restart (stop+start) into UI)
    @Override
    public void start()
    {


//        ServerLocator serverLocator = HornetQClient.createServerLocatorWithoutHA(new TransportConfiguration(InVMConnectorFactory.class.getName()));
//        serverLocator.setReconnectAttempts(-1);
//        try {
//            serverLocator.createSessionFactory();
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e); // todo !!! handle
//        }


        pico = new DefaultPicoContainer();

        MongoSyncConfig cfg = new MongoSyncConfig(null); // todo !!! use here some config (use mongo based config that manages through web UI)

        pico.addComponent(MongoSyncConfig.class, cfg);
        pico.addComponent(EmitterConfig.class, cfg.getEmitterConfig());
        pico.addComponent(ReceiverConfig.class, cfg.getReceiverConfig());
        pico.addComponent(MongoClient.class, mongo);

        pico.start();
    }

    @Override
    public void stop()
    {

        pico.stop();
    }

}
