package ru.beta2.platform.core.config;

/**
 * User: Inc
 * Date: 18.02.14
 * Time: 17:58
 */
public interface ConfigService extends ConfigServiceCover
{

    Config getConfig(String name);

}
