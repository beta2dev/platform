package ru.beta2.platform.mongosync.protocol;

import java.util.Set;

/**
 * User: inc
 * Date: 02.04.14
 * Time: 14:04
 */
public class CloneCollectionsMessage extends ProtocolMessage
{

    private String hostname;
    private Set<String> collections;

    public CloneCollectionsMessage(String hostname, Set<String> collections)
    {
        this.hostname = hostname;
        this.collections = collections;
    }

    public String getHostname()
    {
        return hostname;
    }

    public Set<String> getCollections()
    {
        return collections;
    }
}
