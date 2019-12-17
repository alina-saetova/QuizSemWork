package com.itis.kpfu;

public interface ClientConnectionListener {

    void onConnectionReady(ClientConnection connection);
    void onReceive(ClientConnection connection, String answer);
    void onDisconnect(ClientConnection connection);
}
