package RPC_Project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ProfilePage {

    private Main app;
    private Scene scene;

    public ProfilePage(Main app) {
        this.app = app;
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #2b3947;");

        Text usernameText = new Text(app.getSender().getUsername());
        usernameText.setFont(Font.font(24));
        usernameText.setFill(Color.WHITE);

        Text emailText = new Text(app.getSender().getEmail());
        usernameText.setFont(Font.font(24));
        usernameText.setFill(Color.WHITE);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            app.showChatUI(app.getSender().getUsername());
        });
        backBtn.setPrefWidth(150);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            app.showLoginPage();
        });
        logoutBtn.setPrefWidth(150);

        root.getChildren().addAll(usernameText, emailText, backBtn, logoutBtn);

        scene = new Scene(root, 1000, 600);
    }

    public Scene getScene() {
        return scene;
    }
}
