package ru.beta2.platform.mongosync.protocol;

import org.apache.commons.lang.StringUtils;
import org.hornetq.api.core.client.ClientMessage;

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

    @Override
    public ClientMessage toHornetQMessage(ClientMessage message)
    {
        message.putStringProperty("cmd", "clone");
        message.putStringProperty("hostname", hostname);
        message.putStringProperty("collections", StringUtils.join(collections, ','));
        return message;
    }

    @Override
    public String toString()
    {
        return "CloneCollectionsMessage{" +
                "hostname='" + hostname + '\'' +
                ", collections=" + collections +
                '}';
    }
}
