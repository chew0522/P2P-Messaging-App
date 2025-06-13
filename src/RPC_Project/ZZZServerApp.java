package RPC_Project;

import java.io.*;
import java.net.*;

public class ZZZServerApp {
    public static final int port = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                String type;
                try {
                    type = dis.readUTF();
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                }

                if (type.equals("TEXT")) {
                    String message = dis.readUTF();
                    System.out.println("Text received: " + message);
                    dos.writeUTF("Received: " + message);
                } else if (type.equals("FILE")) {
                    String filename = dis.readUTF();
                    long fileSize = dis.readLong();
                    File file = new File("received_" + filename);

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        long remaining = fileSize;

                        while (remaining > 0 && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                            fos.write(buffer, 0, read);
                            remaining -= read;
                        }
                    }

                    System.out.println("File received: " + filename);
                    dos.writeUTF("File received successfully.");
                } else {
                    System.out.println("Unknown type received: " + type);
                }
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}        
