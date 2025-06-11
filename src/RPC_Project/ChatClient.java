package RPC_Project;
import java.io.*;
import java.net.*;

public class ChatClient {
    public static final int PORT = 5000;
    Socket socket;
    String serverIP;
    DataInputStream dis;
    DataOutputStream dos;

    ChatClient(String serverIP){
        this.serverIP = serverIP;
    }

    public void setServerIP(String serverIP){
        this.serverIP = serverIP;
    }

    public String getServerIp(){
        return this.serverIP;
    }

    public void connect() throws IOException {
        socket = new Socket(serverIP, PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("Connected to server " + serverIP + " on port " + PORT);
    }

    public void sendText(String message) throws IOException {
        dos.writeUTF("TEXT");
        dos.writeUTF(message);
        System.out.println("Server: " + dis.readUTF());
    }

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

        System.out.println("Server: " + dis.readUTF());
    }

    // Add this to ChatClient.java

    public String receiveText() throws IOException {
        String type = dis.readUTF();
        if (!type.equals("TEXT")) {
            throw new IOException("Expected TEXT, but received: " + type);
        }
        return dis.readUTF();
    }

    public void receiveFile(String saveDirectory) throws IOException {
        String type = dis.readUTF();
        if (!type.equals("FILE")) {
            throw new IOException("Expected FILE, but received: " + type);
        }

        String filename = dis.readUTF();
        long fileSize = dis.readLong();

        File saveFile = new File(saveDirectory, "received_" + filename);
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
    }


    public void close() throws IOException {
        dis.close();
        dos.close();
        socket.close();
    }
}
