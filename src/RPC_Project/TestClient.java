package RPC_Project;

import java.io.IOException;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) {
        ChatClient client = new ChatClient("192.168.165.163"); // Update if using a different server IP

        try {
            client.connect(); // Connect to the server
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("You: ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    break; // Exit loop and close connection
                }

                client.sendText(message); // Send message
                String reply = client.receiveText(); // Wait for reply from server
                System.out.println("Server: " + reply);
            }

            client.close(); // Close resources
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
