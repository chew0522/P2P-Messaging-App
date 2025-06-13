package RPC_Project;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {

    private DatabaseManager dbManager;
    private Stage primaryStage;
    private String url = "jdbc:mysql://localhost:3306/CNS4400";
    private String user = "root";
    private String password = "223763";
    private PeerClientClass activeChatClient;
    private PeerClientClass activeChatServer;
    private User activeSender;
    private User activeReceiver;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        try {
            // Instantiate DatabaseManager with connection details
            dbManager = new DatabaseManager(url, user, password);

            // Connect to the database
            dbManager.connect();

            // Show Login page first
            showIPAddressInput();
            stage.show();

        } catch (SQLException e) {
            System.err.println("Application startup failed due to database connection error: " + e.getMessage());
            e.printStackTrace();
            // Show an alert to the user or exit the application gracefully
            // Example:
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Could not connect to the database.");
            alert.setContentText("Please ensure the database server is running and the connection details are correct.\n" + e.getMessage());
            alert.showAndWait();
            Platform.exit(); // Exit the JavaFX application
        }
        
    }

    public interface ConnectionCallback {
        void onConnectionSuccess(PeerClientClass server, PeerClientClass client);
        void onConnectionFailed();
    }

    public interface LoginCallBack{
        void LoginSuccess(User sender, User receiver);
        void LoginFailed();

    }
    
    public void showIPAddressInput() {
        IPAddressInput ipInputUI = new IPAddressInput(this, new ConnectionCallback() {
            @Override
            public void onConnectionSuccess(PeerClientClass server, PeerClientClass client) {
                // When connection is successful, store the client and proceed
                activeChatServer = server;
                activeChatClient = client; // Store the client in Main
                System.out.println("Callback: Connection successful! Navigating to Login Page.");
                showLoginPage(); // <--- NEW: Call method to show Chat UI
            }

            @Override
            public void onConnectionFailed() {
                System.out.println("Callback: Connection failed.");
                // IPAddressInputUI already shows an alert, so nothing more needed here unless Main has specific error UI
            }
        });
        primaryStage.setScene(ipInputUI.getScene());
        primaryStage.setTitle("Connecting to Server...");
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this, new LoginCallBack() {
            @Override
            public void LoginSuccess(User sender, User receiver){
                activeSender = sender;
                activeReceiver = receiver;
            }

            @Override
            public void LoginFailed(){
                System.out.println("Callback: Login Failed");
            }
        });
        primaryStage.setScene(loginPage.getScene());
        primaryStage.setTitle("Login");
    }

    public void showChatUI(String username) {
        ChatUI chatUI = new ChatUI(this);
        chatUI.setPrimaryStage(primaryStage); 
        primaryStage.setScene(chatUI.getScene());
        primaryStage.setTitle("Chat - " + username);
    }


    public void showProfilePage() {
        ProfilePage profilePage = new ProfilePage(this);
        primaryStage.setScene(profilePage.getScene());
        primaryStage.setTitle("Profile");
    }

    public void showSignUpPage() {
        SignUpPage signUpPage = new SignUpPage(this);
        primaryStage.setScene(signUpPage.getScene());
        primaryStage.setTitle("Sign Up");
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    public PeerClientClass getActiveServer(){
        return activeChatServer;
    }

    public PeerClientClass getActiveClient(){
        return activeChatClient;
    }

    public User getSender(){
        return activeSender;
    }

    public User getReceiver(){
        return activeReceiver;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
