package ru.beta2.platform.mongosync.emitter;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.io.Serializable;
import java.util.Set;

/**
 * todo !!! remove ?
 * User: inc
 * Date: 01.04.14
 * Time: 21:23
 */
public class EmitRoute implements Serializable
{

    @Id @ObjectId
    private String id;
    private String address;
    private boolean active;
    private Set<String> collections;

    // todo !!! add toString()

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public Set<String> getCollections()
    {
        return collections;
    }

    public void setCollections(Set<String> collections)
    {
        this.collections = collections;
    }

}
