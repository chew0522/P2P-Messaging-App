package RPC_Project;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class IPAddressInput {

    private Scene scene;
    private Main app;
    private PeerClientClass currentChatServer;
    private PeerClientClass currentChatClient;
    private Main.ConnectionCallback connectionCallback;
    private boolean hasAccepted = false; 
    private boolean hasConnected = false; 
    private String ipAddress = "";

    public IPAddressInput(Main app, Main.ConnectionCallback callback) {
        this.app = app;
        this.connectionCallback = callback;
        createScene();
    }

    public void createScene() {
        // --- UI Elements ---

        Label ipLabel = new Label("Enter IP address:");
        ipLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        ipLabel.setTextFill(Color.web("#ecf0f1"));

        TextField ipTextField = new TextField();
        ipTextField.setPromptText("e.g., 192.168.1.1");
        ipTextField.setPrefWidth(300);
        ipTextField.setMaxWidth(300);
        ipTextField.setStyle(
            "-fx-background-color: #3b5066;" +
            "-fx-text-fill: #ecf0f1;" +
            "-fx-font-size: 18px;" +
            "-fx-prompt-text-fill: #95a5a6;" +
            "-fx-border-color: #2c3e50;" +
            "-fx-border-radius: 5px;" +
            "-fx-background-radius: 5px;"
        );

        Button connectButton = new Button("Connect");
        connectButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        connectButton.setTextFill(Color.WHITE);
        connectButton.setPrefWidth(150);
        connectButton.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #2ecc71;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-width: 2px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 0);"
        );

        connectButton.setOnMouseEntered(e -> connectButton.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #27ae60;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-width: 2px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 15, 0, 0, 0);"
        ));

        connectButton.setOnMouseExited(e -> connectButton.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #2ecc71;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-width: 2px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 0);"
        ));

        

        connectButton.setOnAction(event -> {
            ipAddress = ipTextField.getText().trim();

            if (ipAddress.isEmpty()) {
                System.out.println("IP address is empty.");
                return;
            }

            connectButton.setDisable(true);

            // Start server side listening in the background
            new Thread(() -> {
                try {
                    currentChatServer = PeerClientClass.waitForConnection();
                    System.out.println("Incoming connection established.");

                    hasAccepted = true;
                    checkAndNotify();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    currentChatClient = PeerClientClass.connectTo(ipAddress);
                    hasConnected = true;
                    checkAndNotify();
                } catch (IOException e) {
                    System.err.println("Failed to connect: " + e.getMessage());
                }
                
            }).start();
        });
        connectButton.setDisable(false);

        Image image = new Image(getClass().getResource("/images/IPAddressLogo.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setBackground(new Background(new BackgroundFill(
            Color.web("#334965"), CornerRadii.EMPTY, Insets.EMPTY
        )));
        root.getChildren().addAll(imageView, ipLabel, ipTextField, connectButton);

        scene = new Scene(root, 1000, 600);

        
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

   private void checkAndNotify() {
        Platform.runLater(() -> {
            if (hasConnected && hasAccepted) {
                showAlert(AlertType.INFORMATION, "Connection Status", "Connected!",
                    "Successfully connected to " + ipAddress + ".");
                if (connectionCallback != null) {
                    connectionCallback.onConnectionSuccess(currentChatServer, currentChatClient);
                }
            } else {
                // Delay error alert to give time for potential connection completion
                PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
                delay.setOnFinished(event -> {
                    // Re-check after delay to avoid false failure
                    if (!(hasConnected && hasAccepted)) {
                        showAlert(AlertType.ERROR, "Connection Status", "Connection Failed!",
                            "Could not connect to " + ipAddress + ".\nPlease ensure the server is running and the IP is correct.");
                        if (connectionCallback != null) {
                            connectionCallback.onConnectionFailed();
                        }
                    }
                });
                delay.play();
            }
        });
    }

}
