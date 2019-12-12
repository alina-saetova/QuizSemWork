package connection;

import java.io.*;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;
    private Thread thread;
    private ClientConnectionListener eventListener;
    private BufferedReader in;
    private BufferedWriter out;


    public ClientConnection(ClientConnectionListener eventListener, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread = new Thread(new ConnRunnable());
        thread.start();
    }

    public synchronized void sendString(StringBuffer data) {
        try {
            out.write(data.toString());
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private class ConnRunnable implements Runnable {
        @Override
        public void run() {
            eventListener.onConnectionReady(ClientConnection.this);
            try {
                while (!thread.isInterrupted()) {
                    String input = in.readLine();
                    eventListener.onReceive(ClientConnection.this, input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
