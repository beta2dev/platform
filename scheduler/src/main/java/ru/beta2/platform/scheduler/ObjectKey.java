package ru.beta2.platform.scheduler;

import java.io.Serializable;

/**
 * User: inc
 * Date: 20.03.14
 * Time: 19:51
 */
public class ObjectKey implements Serializable
{

    private String name;
    private String group;

    public ObjectKey()
    {
    }

    public ObjectKey(String name, String group)
    {
        this.name = name;
        this.group = group;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    @Override
    public String toString()
    {
        return "ObjectKey{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
