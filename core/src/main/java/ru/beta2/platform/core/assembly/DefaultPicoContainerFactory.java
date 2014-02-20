package ru.beta2.platform.core.assembly;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 14:29
 */
public class DefaultPicoContainerFactory implements PicoContainerFactory
{

    private final PicoContainer parent;

    public DefaultPicoContainerFactory(PicoContainer parent)
    {
        this.parent = parent;
    }

    @Override
    public MutablePicoContainer createPicoContainer()
    {
        return new DefaultPicoContainer(parent);
    }
}
