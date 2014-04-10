package ru.beta2.platform.mongosync.protocol;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hornetq.api.core.client.ClientMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * User: inc
 * Date: 02.04.14
 * Time: 14:04
 */
public class CloneCollectionsMessage extends ProtocolMessage
{

    public static final String CMDNAME = "clone";

    private String hostname;
    private Set<String> collections;

    public CloneCollectionsMessage(String hostname, Set<String> collections)
    {
        this.hostname = hostname;
        this.collections = collections;
    }

    public CloneCollectionsMessage(ClientMessage msg)
    {
        hostname = msg.getStringProperty("hostname");
        collections = new HashSet<String>();
        CollectionUtils.addAll(collections, msg.getStringProperty("collections").split(","));
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
        message.putStringProperty("cmd", CMDNAME);
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
