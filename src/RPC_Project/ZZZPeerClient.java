package RPC_Project;

import java.io.*;
import java.net.*;

public class ZZZPeerClient {

    private static final int LISTEN_PORT = 6000; // Port to listen on
    private static final String PEER_IP = "192.168.165.163"; // Change to peer’s IP
    private static final int PEER_PORT = 6000;

    public static void main(String[] args) {
        // 1. Start listener thread (server part)
        new Thread(() -> startListener()).start();

        // 2. Start sender (client part)
        try (Socket socket = new Socket(PEER_IP, PEER_PORT)) {
            System.out.println("Connected to peer: " + PEER_IP);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String toSend;
            while ((toSend = consoleReader.readLine()) != null) {
                dos.writeUTF(toSend);
                dos.flush();
            }

        } catch (IOException e) {
            System.out.println("Error connecting to peer: " + e.getMessage());
        }
    }

    private static void startListener() {
        try (ServerSocket serverSocket = new ServerSocket(LISTEN_PORT)) {
            System.out.println("Listening on port " + LISTEN_PORT + "...");

            Socket socket = serverSocket.accept();
            System.out.println("Peer connected to me!");

            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String message;
            while ((message = dis.readUTF()) != null) {
                System.out.println("Peer: " + message);
            }

        } catch (IOException e) {
            System.out.println("Listener stopped: " + e.getMessage());
        }
    }
}

