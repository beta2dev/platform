package ru.beta2.platform.mongosync.emitter;

import java.util.Collection;

/**
 * todo !!! remove
 * User: inc
 * Date: 01.04.14
 * Time: 21:33
 */
public interface RouteManagerCover
{

    Collection<EmitRoute> getRoutes();

    void addRoute(EmitIntent intent);

    void removeRoute(String routeId);

    void activateRoute(String routeId);

    void passivateRoute(String routeId);

}
