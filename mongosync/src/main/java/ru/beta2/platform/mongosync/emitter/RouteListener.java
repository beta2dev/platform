package ru.beta2.platform.mongosync.emitter;

/**
 * todo !!! remove
 * User: inc
 * Date: 01.04.14
 * Time: 21:39
 */
public interface RouteListener
{

    void routeActivated(EmitRoute route);

    void routePassivated(EmitRoute route);

}
