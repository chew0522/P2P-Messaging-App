package RPC_Project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;

public class ZZZChatAttachmentMenu extends Application {

    private Popup popup;

    @Override
    public void start(Stage primaryStage) {
        ImageView attachIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/attach_icon.png"))); // Add your attach icon
        attachIcon.setFitHeight(24);
        attachIcon.setFitWidth(24);

        attachIcon.setOnMouseClicked(e -> showAttachmentMenu(primaryStage, attachIcon));

        VBox root = new VBox(10, attachIcon);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Attachment Menu Demo");
        primaryStage.show();
    }

    private void showAttachmentMenu(Stage stage, ImageView anchorNode) {
        VBox menuBox = new VBox(10);
        menuBox.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-border-color: lightgray; -fx-background-radius: 10; -fx-border-radius: 10;");

        // Menu Items
        menuBox.getChildren().add(createMenuItem("🖼 Photos & Videos", () -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Photos or Videos");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images and Videos", "*.png", "*.jpg", "*.jpeg", "*.mp4", "*.mov", "*.avi")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                System.out.println("Selected file: " + file.getAbsolutePath());
            }
            popup.hide();
        }));

        menuBox.getChildren().add(createMenuItem("📄 Document", () -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Document");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                System.out.println("Selected file: " + file.getAbsolutePath());
            }
            popup.hide();
        }));

        // Show Popup
        popup = new Popup();
        popup.getContent().add(menuBox);
        popup.setAutoHide(true);
        popup.show(stage, 
            anchorNode.localToScreen(0, 0).getX(), 
            anchorNode.localToScreen(0, 0).getY() - 100
        );
    }

    private HBox createMenuItem(String labelText, Runnable onClickAction) {
        Label icon = new Label(labelText.split(" ")[0]);
        icon.setFont(Font.font(16));

        Label label = new Label(labelText.substring(labelText.indexOf(" ") + 1));
        label.setFont(Font.font(14));
        label.setTextFill(Color.BLACK);

        HBox item = new HBox(10, icon, label);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8));
        item.setStyle("-fx-cursor: hand;");

        item.setOnMouseClicked(e -> onClickAction.run());

        return item;
    }

    public static void main(String[] args) {
        launch(args);
    }
}