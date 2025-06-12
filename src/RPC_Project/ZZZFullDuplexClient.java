package RPC_Project;

import java.io.*;
import java.net.*;

public class ZZZFullDuplexClient {
    public static void main(String[] args) {
        final String SERVER_IP = "192.168.165.163"; // Or server's IP address
        final int PORT = 5000;

        try (Socket socket = new Socket(SERVER_IP, PORT)) {
            System.out.println("Connected to server.");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Thread to receive messages from server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = dis.readUTF()) != null) {
                        System.out.println("Server: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Server disconnected.");
                }
            }).start();

            // Thread to send messages to server
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String toSend;
            while ((toSend = consoleReader.readLine()) != null) {
                dos.writeUTF(toSend);
                dos.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

