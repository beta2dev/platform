package ru.beta2.platform.mongosync.emitter.emit;

import java.util.Collection;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:12
 */
public interface EmitManagerCover
{

    Collection<EmitInfo> getEmits();

    void startEmit(String address, String... namespaces);

    void stopEmitAll(String address);

    void stopEmit(String address, String... namespaces);

    void changeEmit(String address, String... namespaces);

}
