package RPC_Project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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

        ImageView userPhoto = new ImageView(new Image("file:///C:/Users/raymo/eclipse-workspace/CNS4400/resources/user_photo.jpg"));
        userPhoto.setFitWidth(120);
        userPhoto.setFitHeight(120);
        userPhoto.setPreserveRatio(true);
        userPhoto.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0); -fx-background-radius: 60;");

        Text usernameText = new Text("Raymond Ng");
        usernameText.setFont(Font.font(24));
        Text phoneText = new Text("01128084108");
        phoneText.setFont(Font.font(16));
        phoneText.setStyle("-fx-text-fill: gray;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> app.showLoginPage());
        logoutBtn.setPrefWidth(150);

        root.getChildren().addAll(userPhoto, usernameText, phoneText, logoutBtn);

        scene = new Scene(root, 400, 400);
    }

    public Scene getScene() {
        return scene;
    }
}
