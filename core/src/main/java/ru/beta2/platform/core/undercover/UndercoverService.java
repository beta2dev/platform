package ru.beta2.platform.core.undercover;

import ru.beta2.platform.core.util.HandlerRegistration;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 17:05
 */
public interface UndercoverService
{

    HandlerRegistration registerCover(String path, Class intf, Object impl);

}
