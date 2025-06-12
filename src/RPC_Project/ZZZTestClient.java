package RPC_Project;

import java.io.IOException;
import java.util.Scanner;



public class ZZZTestClient {
    private static final int PORT = 5000;
    private static PeerClientClass server;
    private static PeerClientClass client;
    public static void main(String[] args) {

        new Thread(() -> {
            try {
                server = PeerClientClass.waitForConnection(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        

        new Thread(() -> {
            try {
                while (true) {
                    System.out.println(server.receiveText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        Scanner input = new Scanner(System.in);
        try {
            client = PeerClientClass.connectTo("localhost", PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        while (true) {
            System.out.println("You: ");
            String text = input.nextLine();
            try {
                client.sendMessage(text);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
}
