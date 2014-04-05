package ru.beta2.platform.mongosync.emitter;

import java.util.Set;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:18
 */
public interface EmitListener
{

    void onEmitStart(String address, Set<String> namespaces);

    void onEmitStop(String address, Set<String> namespaces);

}
