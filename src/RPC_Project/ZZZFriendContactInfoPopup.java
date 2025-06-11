package RPC_Project;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ZZZFriendContactInfoPopup {

    private Stage stage;
    private BorderPane root;

    // Data for the friend
    private String friendName;
    private String phone;
    private String email;
    private List<String> files;
    private List<String> media;
    private List<String> links;

    public ZZZFriendContactInfoPopup(String friendName, String phone, String email,
                                  List<String> files, List<String> media, List<String> links) {
        this.friendName = friendName;
        this.phone = phone;
        this.email = email;
        this.files = files;
        this.media = media;
        this.links = links;

        stage = new Stage();
        root = new BorderPane();

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(300);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton overviewBtn = new RadioButton("Overview");
        overviewBtn.setToggleGroup(toggleGroup);
        overviewBtn.setSelected(true);

        RadioButton filesBtn = new RadioButton("Files");
        filesBtn.setToggleGroup(toggleGroup);

        RadioButton mediaBtn = new RadioButton("Media");
        mediaBtn.setToggleGroup(toggleGroup);

        RadioButton linksBtn = new RadioButton("Links");
        linksBtn.setToggleGroup(toggleGroup);

        sidebar.getChildren().addAll(overviewBtn, filesBtn, mediaBtn, linksBtn);

        StackPane contentPane = new StackPane();
        contentPane.setPadding(new Insets(10));

        VBox overviewContent = createOverviewContent();
        VBox filesContent = createFilesContent();
        VBox mediaContent = createMediaContent();
        VBox linksContent = createLinksContent();

        contentPane.getChildren().addAll(overviewContent, filesContent, mediaContent, linksContent);

        overviewContent.setVisible(true);
        filesContent.setVisible(false);
        mediaContent.setVisible(false);
        linksContent.setVisible(false);

        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            overviewContent.setVisible(newToggle == overviewBtn);
            filesContent.setVisible(newToggle == filesBtn);
            mediaContent.setVisible(newToggle == mediaBtn);
            linksContent.setVisible(newToggle == linksBtn);
        });

        root.setLeft(sidebar);
        root.setCenter(contentPane);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle(friendName + " - Contact Info");
    }

    public void show() {
        stage.show();
    }

    private VBox createOverviewContent() {
        VBox box = new VBox(10);
        box.getChildren().add(new Label("Name: " + friendName));
        box.getChildren().add(new Label("Phone: " + phone));
        box.getChildren().add(new Label("Email: " + email));

        CheckBox muteCheckBox = new CheckBox("Mute notifications");
        Button blockBtn = new Button("Block Contact");

        box.getChildren().addAll(muteCheckBox, blockBtn);
        return box;
    }

    private VBox createFilesContent() {
        VBox box = new VBox(10);
        box.getChildren().add(new Label("Files shared with " + friendName + ":"));
        if (files != null && !files.isEmpty()) {
            for (String file : files) {
                box.getChildren().add(new Label(file));
            }
        } else {
            box.getChildren().add(new Label("No files available"));
        }
        return box;
    }

    private VBox createMediaContent() {
        VBox box = new VBox(10);
        box.getChildren().add(new Label("Media shared with " + friendName + ":"));
        if (media != null && !media.isEmpty()) {
            for (String m : media) {
                box.getChildren().add(new Label(m));
            }
        } else {
            box.getChildren().add(new Label("No media available"));
        }
        return box;
    }

    private VBox createLinksContent() {
        VBox box = new VBox(10);
        box.getChildren().add(new Label("Links shared with " + friendName + ":"));
        if (links != null && !links.isEmpty()) {
            for (String link : links) {
                box.getChildren().add(new Label(link));
            }
        } else {
            box.getChildren().add(new Label("No links available"));
        }
        return box;
    }
}

