package RPC_Project;

import java.io.*;
import java.net.*;

public class PeerClientClass {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String filename;
    private final static int PORT = 5000;

    public PeerClientClass(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    // Connect client to server 
    public static PeerClientClass connectTo(String ip) throws IOException {
        Socket socket = new Socket(ip, PORT);
        System.out.println("Client connected to " + ip);
        return new PeerClientClass(socket);
    }

    // Start server 
    public static PeerClientClass waitForConnection() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket = serverSocket.accept();
        System.out.println("Server running... ");
        return new PeerClientClass(socket);
    }

    // Close server or client connection 
    public void close() throws IOException {
        dis.close();
        dos.close();
        socket.close();
    }

    // Send String message to server 
    public void sendText(String message) throws IOException {
        dos.writeUTF("TEXT");
        dos.writeUTF(message);
        System.out.println("Sent:" + message);
    }

    // Send File message to server 
    public void sendFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Invalid file path.");
            return;
        }

        dos.writeUTF("FILE");
        dos.writeUTF(file.getName());
        dos.writeLong(file.length());

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int read;
        while ((read = fis.read(buffer)) > 0) {
            dos.write(buffer, 0, read);
        }
        fis.close();
    }

    // Receive String message from server 
    public String receiveText() throws IOException {
        String message = dis.readUTF();
        System.out.println("Received: " + message); 
        
        return message;
    }

    // Receive File message from server 
    public File receiveFile(String saveDirectory) throws IOException {
        String filename = dis.readUTF();
        long fileSize = dis.readLong();

        File saveFile = new File(filename);
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            byte[] buffer = new byte[4096];
            int read;
            long remaining = fileSize;

            while (remaining > 0 && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                fos.write(buffer, 0, read);
                remaining -= read;
            }
        }

        System.out.println("File received and saved to: " + saveFile.getAbsolutePath());
        return saveFile;
    }


    public String getFileName(){
        return filename;
    }

    public DataInputStream getDIS(){
        return dis;
    }

    public DataOutputStream getDOS(){
        return dos;
    }
}

