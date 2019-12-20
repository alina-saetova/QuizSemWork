package com.itis.kpfu;

import java.io.*;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;
    private Thread thread;
    private ClientConnectionListener eventListener;
    private BufferedReader in;
    private BufferedWriter out;
    private  String name;

    public String getName() {
        return name;
    }

    public ClientConnection(ClientConnectionListener eventListener, Socket socket) throws IOException {
        name = "player" + System.currentTimeMillis();
        System.out.println(name + " " + ClientConnection.this.name);
        this.socket = socket;
        this.eventListener = eventListener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread = new Thread(() -> {
            eventListener.onConnectionReady(ClientConnection.this);
            try {
                while (!thread.isInterrupted()) {
                    String input = in.readLine();
                    eventListener.onReceive(ClientConnection.this, input);
                }
            } catch (IOException e) {
                System.out.println("TCPConnection exception " + e);
            } finally {
                eventListener.onDisconnect(ClientConnection.this);
            }
        });
        thread.start();
    }

    public synchronized void sendString(StringBuffer data) {
        try {
            out.write(data.toString() + "\r\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
