package ru.beta2.platform.mongosync.emitter;

import com.mongodb.MongoClient;
import org.apache.commons.collections4.IteratorUtils;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.beta2.platform.core.undercover.CoverRegistrable;
import ru.beta2.platform.core.util.HandlerRegistration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * todo !!! remove
 * User: inc
 * Date: 01.04.14
 * Time: 21:29
 */
public class RouteManager implements RouteManagerCover, CoverRegistrable
{

    private final Logger log = LoggerFactory.getLogger(RouteManager.class);

    private final EmitterConfig cfg;
    private final MongoClient mongo;
    private final Collection<RouteListener> listeners = new ArrayList<RouteListener>();

    private final RouteListener listenersDispatcher = new RouteListener()
    {
        @Override
        public void routeActivated(EmitRoute route)
        {
            for (RouteListener l : listeners) {
                l.routeActivated(route);
            }
        }

        @Override
        public void routePassivated(EmitRoute route)
        {
            for (RouteListener l : listeners) {
                l.routePassivated(route);
            }
        }
    };

    private MongoCollection routes;

    public RouteManager(EmitterConfig cfg, MongoClient mongo)
    {
        this.cfg = cfg;
        this.mongo = mongo;

        routes = new Jongo(mongo.getDB(cfg.getRoutesDbName())).getCollection(cfg.getRoutesCollectionName());
    }

    public HandlerRegistration addListener(final RouteListener listener)
    {
        listeners.add(listener);
        return new HandlerRegistration()
        {
            @Override
            public void removeHandler()
            {
                listeners.remove(listener);
            }
        };
    }

    @Override
    public Collection<EmitRoute> getRoutes()
    {
        log.trace("Get routes");
        return IteratorUtils.toList(routes.find().as(EmitRoute.class).iterator());
    }

    public Collection<EmitRoute> getActiveRoutes()
    {
        log.trace("Get active routes");
        return IteratorUtils.toList(routes.find("{active: true}").as(EmitRoute.class).iterator());
    }

    @Override
    public void addRoute(EmitIntent intent)
    {
        log.debug("Add route: {}", intent);
        EmitRoute route = new EmitRoute();
        route.setAddress(intent.getAddress());
        route.setCollections(intent.getCollections());
        save(route);
    }

    @Override
    public void removeRoute(String routeId)
    {
        log.debug("Remove route {}", routeId);
        EmitRoute route = obtainRoute(routeId);
        if (route.isActive()) {
            log.trace("Passivate route before remove");
            listenersDispatcher.routePassivated(route);
        }
        routes.remove(new ObjectId(routeId)); // todo ??? handle WriteResult ?
    }

    @Override
    public void activateRoute(String routeId)
    {
        log.debug("Activate route {}", routeId);
        EmitRoute route = obtainRoute(routeId);

        if (route.isActive()) {
            log.trace("Route already active");
            return;
        }

        route.setActive(true);
        save(route);

        listenersDispatcher.routeActivated(route);
    }

    @Override
    public void passivateRoute(String routeId)
    {
        log.debug("Passivate route {}", routeId);
        EmitRoute route = obtainRoute(routeId);

        if (!route.isActive()) {
            log.trace("Route is not active");
            return;
        }

        route.setActive(false);
        save(route);

        listenersDispatcher.routePassivated(route);
    }

    @Override
    public String getCoverPath()
    {
        return "/mongosync-emitter-routemanager";
    }

    @Override
    public Class getCoverInterface()
    {
        return RouteManagerCover.class;
    }

    //
    //  Implementation details
    //

    private EmitRoute obtainRoute(String routeId)
    {
        EmitRoute route = routes.findOne(new ObjectId(routeId)).as(EmitRoute.class);
        if (route == null) {
            throw new IllegalArgumentException("EmitRoute not found with id '" + routeId + "'");
        }
        return route;
    }

    private void save(EmitRoute route)
    {
        routes.save(route); // todo ??? process WriteResult ?
    }

}
