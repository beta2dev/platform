package ru.beta2.platform.core.config;

import java.util.List;

/**
 * User: Inc
 * Date: 20.02.14
 * Time: 16:53
 */
public interface ConfigServiceCover
{

    List<String> getConfigNames();

    String getConfigValue(String name);

    void setConfigValue(String name, String value);

}
