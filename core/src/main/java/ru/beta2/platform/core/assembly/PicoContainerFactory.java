package ru.beta2.platform.core.assembly;

import org.picocontainer.MutablePicoContainer;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 13:58
 */
public interface PicoContainerFactory
{
    MutablePicoContainer createPicoContainer();
}
