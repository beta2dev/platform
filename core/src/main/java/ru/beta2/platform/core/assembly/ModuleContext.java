package ru.beta2.platform.core.assembly;

import org.picocontainer.MutablePicoContainer;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 13:14
 */
public interface ModuleContext
{

    MutablePicoContainer getApplicationContainer();

}
