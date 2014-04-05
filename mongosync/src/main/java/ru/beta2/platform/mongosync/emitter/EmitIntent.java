package ru.beta2.platform.mongosync.emitter;

import java.io.Serializable;
import java.util.Set;

/**
 * todo !!! remove
 * User: inc
 * Date: 02.04.14
 * Time: 13:06
 */
public class EmitIntent implements Serializable
{

    private String address;
    private Set<String> collections;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Set<String> getCollections()
    {
        return collections;
    }

    public void setCollections(Set<String> collections)
    {
        this.collections = collections;
    }

    @Override
    public String toString()
    {
        return "EmitIntent{" +
                "address='" + address + '\'' +
                ", collections=" + collections +
                '}';
    }
}
