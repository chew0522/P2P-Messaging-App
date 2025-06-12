package RPC_Project;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class PeerClientClass {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Thread listenerThread;

    public PeerClientClass(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    public static PeerClientClass connectTo(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        return new PeerClientClass(socket);
    }

    public static PeerClientClass waitForConnection(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();
        return new PeerClientClass(socket);
    }

    public void sendMessage(String message) throws IOException {
        dos.writeUTF(message);
    }

    public void startListening(Consumer<String> messageHandler) {
        listenerThread = new Thread(() -> {
            try {
                while (true) {
                    String msg = dis.readUTF();
                    messageHandler.accept(msg);
                }
            } catch (IOException e) {
                messageHandler.accept("Disconnected: " + e.getMessage());
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void close() throws IOException {
        dis.close();
        dos.close();
        socket.close();
    }

    public void sendText(String message) throws IOException {
        dos.writeUTF("TEXT");
        dos.writeUTF(message);
    }
}

