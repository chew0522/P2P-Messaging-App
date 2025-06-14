package RPC_Project;

import java.io.IOException;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LoginPage {

    private Scene scene;
    private Main app;
    private DatabaseManager dbManager;
    private Main.LoginCallBack loginCallBack;
    private PeerClientClass server;
    private PeerClientClass client;
    private User sender;
    private User receiver;

    public LoginPage(Main app, Main.LoginCallBack callBack) {
        this.app = app;
        this.dbManager = app.getDatabaseManager();
        this.loginCallBack = callBack;
        this.server = app.getActiveServer();
        this.client = app.getActiveClient();
        createScene();
    }

    private void createScene() {
        HBox root = new HBox();
        root.setPrefSize(1000, 600);

        // ========== LEFT PANEL ==========
        VBox formPane = new VBox(15);
        formPane.setPrefWidth(350);
        formPane.setPadding(new Insets(40));
        formPane.setAlignment(Pos.CENTER_LEFT);
        formPane.setStyle("-fx-background-color: #ffffff;");

        Text title = new Text("Welcome Back 👋");
        title.setFont(Font.font("Arial", 32));
        title.setFill(Color.web("#333"));
        title.setStyle("-fx-font-weight: bold;");

        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();
        styleTextField(usernameField);

        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        styleTextField(passwordField);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(120);
        loginBtn.setStyle(
            "-fx-background-color: rgba(47,67,90,255);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        

        loginBtn.setOnAction(e -> {
            String email = usernameField.getText().trim();
            String password = passwordField.getText();
            String senderEmail = "";
            String senderPassword = "";
            try {
                if (email.isEmpty()) {
                    errorLabel.setText("Username is empty.");
                } else if (password.isEmpty()) {
                    errorLabel.setText("Password is empty.");
                } else if (!dbManager.validateLogin(email, password)) {
                    errorLabel.setText("Invalid Username or Password!");
                } else {
                    errorLabel.setText("");
                    String username = dbManager.getUsername(email, password);
                    try {
                        client.getDOS().writeUTF(email);
                        client.getDOS().writeUTF(password);
                        senderEmail = server.getDIS().readUTF();
                        senderPassword = server.getDIS().readUTF();
                        
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    receiver = new User(app, senderEmail, senderPassword);
                    sender = new User(app, email, password);
                    if (loginCallBack != null) {
                        loginCallBack.LoginSuccess(sender, receiver);
                    } else{
                        loginCallBack.LoginFailed();
                    } 

                    app.showChatUI(username);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        Hyperlink signUpLink = new Hyperlink("Not registered? Sign Up.");
        signUpLink.setStyle("-fx-text-fill: #1e88e5;");
        signUpLink.setOnAction(e -> app.showSignUpPage());

        formPane.getChildren().addAll(
            title,
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            errorLabel,
            loginBtn,
            signUpLink
        );

        // ========== RIGHT PANEL ==========
        VBox rightPane = new VBox();
        rightPane.setPrefWidth(650);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setStyle("-fx-background-color: rgba(47,67,90,255);");

        Image image = new Image(getClass().getResource("/images/LoginPage.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setPreserveRatio(true);
        imageView.setTranslateY(-30); // Negative value moves it upward


        rightPane.getChildren().add(imageView);

        root.getChildren().addAll(formPane, rightPane);
        scene = new Scene(root, 1000, 600);
    }

    private void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-radius: 6;" +
            "-fx-border-radius: 6;" +
            "-fx-border-color: #ccc;" +
            "-fx-padding: 8;" +
            "-fx-font-size: 14px;"
        );
        field.setPrefWidth(250);
    }

    public Scene getScene() {
        return scene;
    }
}
