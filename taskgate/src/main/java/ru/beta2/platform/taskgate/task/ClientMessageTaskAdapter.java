package ru.beta2.platform.taskgate.task;

import org.hornetq.api.core.client.ClientMessage;

import java.util.Set;

/**
 * User: inc
 * Date: 24.03.14
 * Time: 11:28
 */
public class ClientMessageTaskAdapter implements TaskDataAdapter
{

    private final ClientMessage msg;

    public ClientMessageTaskAdapter(ClientMessage msg)
    {
        this.msg = msg;
    }


    @Override
    public String getStringProperty(String key)
    {
        return msg.getStringProperty(key);
    }

    @Override
    public void putStringProperty(String key, String value)
    {
        msg.putStringProperty(key, value);
    }

    @Override
    public Set<? extends CharSequence> getPropertyNames()
    {
        return msg.getPropertyNames();
    }
}
