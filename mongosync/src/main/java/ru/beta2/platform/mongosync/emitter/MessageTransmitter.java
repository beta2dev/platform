package ru.beta2.platform.mongosync.emitter;

import ru.beta2.platform.mongosync.protocol.ProtocolMessage;

import java.util.Set;

/**
 * User: inc
 * Date: 05.04.14
 * Time: 0:25
 */
public interface MessageTransmitter
{

    void sendMessage(String address, ProtocolMessage message) throws TransmitException;

    void sendMessage(Set<String> addresses, ProtocolMessage message) throws TransmitException;

}
