package ru.beta2.platform.mongosync.emitter;

import java.util.Set;

/**
 * User: inc
 * Date: 04.04.14
 * Time: 23:13
 */
public class EmitInfo
{

    private String address;
    private Set<String> namespaces;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Set<String> getNamespaces()
    {
        return namespaces;
    }

    public void setNamespaces(Set<String> namespaces)
    {
        this.namespaces = namespaces;
    }
}
