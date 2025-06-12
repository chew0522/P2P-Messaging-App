package RPC_Project;

import java.io.*;
import java.net.*;

public class ZZZFullDuplexServer {
    public static void main(String[] args) {
        final int PORT = 5000;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for client...");

            Socket socket = serverSocket.accept();
            System.out.println("Client" + socket.getInetAddress() + " connected!");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Thread to receive messages from client
            new Thread(() -> {
                try {
                    String message;
                    while ((message = dis.readUTF()) != null) {
                        System.out.println("Client: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected.");
                }
            }).start();

            // Thread to send messages to client
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

