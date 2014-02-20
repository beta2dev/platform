package ru.beta2.platform.core.config;

import ru.beta2.platform.core.util.HandlerRegistration;

/**
 * User: Inc
 * Date: 19.02.14
 * Time: 23:28
 */
public interface Config
{

    String getName();

    String getValue();

    HandlerRegistration addListener(ConfigListener listener);

}
