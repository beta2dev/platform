package ru.beta2.platform.hornetq.util;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.slf4j.Logger;
import ru.beta2.platform.core.util.LifecycleException;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 14:04
 */
public class SingleSessionHelper
{

    private final Logger log;
    private final ServerLocator serverLocator;

    private ClientSessionFactory sessionFactory;
    private ClientSession session;

    public SingleSessionHelper(Logger log, ServerLocator serverLocator)
    {
        this.log = log;
        this.serverLocator = serverLocator;
    }

    public void create()
    {
        try {
            log.trace("Create HornetQ session factory");
            sessionFactory = serverLocator.createSessionFactory();
        }
        catch (Exception e) {
            log.error("Error create HornetQ session factory", e);
            throw new LifecycleException("Error create HornetQ session factory", e);
        }

        try {
            log.trace("Create HornetQ session");
            session = sessionFactory.createSession();
        }
        catch (HornetQException e) {
            log.error("Error create HornetQ session", e);
            throw new LifecycleException("Error create HornetQ session", e);
        }
    }

    public void start()
    {
        try {
            log.trace("Start HornetQ session");
            session.start();
        }
        catch (HornetQException e) {
            log.error("Error start HornetQ session", e);
            throw new LifecycleException("Error start HornetQ session", e);
        }
    }

    public void stopAndClose()
    {
        try {
            if (session != null) {
                log.trace("Stop and close HornetQ session");
                session.stop();
                session.close();
                session = null;
            }
            if (sessionFactory != null) {
                log.trace("Close HornetQ session factory");
                sessionFactory.close();
                sessionFactory = null;
            }
        }
        catch (HornetQException e) {
            log.warn("Error close HornetQ resource, ignore it", e);
        }
    }

    public ClientSessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public ClientSession getSession()
    {
        return session;
    }
}
