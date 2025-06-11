package RPC_Project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class SignUpPage {

    private Scene scene;
    private Main app;
    private DatabaseManager dbManager;

    public SignUpPage(Main app) {
        this.app = app;
        this.dbManager = app.getDatabaseManager();
        createScene();
    }

    private void createScene() {
        HBox root = new HBox(15);
        root.setPrefSize(1000, 600);
        root.setMaxWidth(1000);
        root.setStyle("-fx-background-color: white;");

        // === Left 2/3: Image ===
        VBox imagePane = new VBox();
        imagePane.setPrefWidth(650);
        imagePane.setAlignment(Pos.CENTER);
        imagePane.setStyle("-fx-background-color: rgba(49,71,94,255);");

        Image image = new Image(getClass().getResource("/images/SignUpPage.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setPreserveRatio(true);
        imageView.setTranslateY(-40); // Negative value moves it upward


        imagePane.getChildren().add(imageView);

        // === Right 1/3: Sign Up Form ===
        
        VBox formPane = new VBox(12);
        formPane.setPrefWidth(350);
        formPane.setPadding(new Insets(40));
        formPane.setAlignment(Pos.CENTER_LEFT);
        formPane.setStyle("-fx-background-color: #ffffff;");


        Text title = new Text("Create Account");
        title.setFont(Font.font("Arial", 26));
        title.setStyle("-fx-font-weight: bold;");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setPrefWidth(120);
        signUpBtn.setStyle("""
                -fx-background-color: #4CAF50;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
        """);

        signUpBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            try {
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    errorLabel.setText("Please fill in all fields.");
                } else if (dbManager.checkUsernameExists(username)) {
                    errorLabel.setText("Username already exists.");
                } else if (dbManager.checkEmailExists(email)) {
                    errorLabel.setText("Email has already been registered.");
                } else if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    errorLabel.setText("Please enter a valid email address.");
                } else if (!password.matches(".*[!@#$%^&*()_+].*")) {
                    errorLabel.setText("Password must include a special character (!@#$%^&*()_+).");
                } else if (!password.equals(confirmPassword)) {
                    errorLabel.setText("Passwords do not match.");
                } else {
                    errorLabel.setText("");
                    dbManager.registerUser(email, confirmPassword, username);
                    app.showChatUI(username);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        Hyperlink signInLink = new Hyperlink("Already have an account? Sign In");
        signInLink.setOnAction(e -> app.showLoginPage());

        VBox.setMargin(signUpBtn, new Insets(10, 0, 0, 0));
        VBox.setMargin(signInLink, new Insets(5, 0, 0, 0));
        VBox.setMargin(errorLabel, new Insets(5, 0, 0, 0));

        formPane.getChildren().addAll(
                title,
                usernameLabel, usernameField,
                emailLabel, emailField,
                passwordLabel, passwordField,
                confirmPasswordLabel, confirmPasswordField,
                signUpBtn,
                errorLabel,
                signInLink
        );

        root.getChildren().addAll(imagePane, formPane);

        

        scene = new Scene(root, 1000, 600);
    }

    public Scene getScene() {
        return scene;
    }
}
