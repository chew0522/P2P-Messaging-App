package RPC_Project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class ZZZChatOverlayPanel extends VBox {

    public ZZZChatOverlayPanel() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        setPrefWidth(300);
    }

    public void showNewChatPanel() {
        this.getChildren().clear();

        Text title = new Text("New Chat");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField searchBar = new TextField();
        searchBar.setPromptText("Enter username or number");

        Label newContact = new Label("➕ New Contact");
        newContact.setStyle("-fx-text-fill: blue; -fx-cursor: hand;");

        VBox contactList = new VBox(10);
        contactList.setPadding(new Insets(10));
        contactList.getChildren().addAll(
                new Label("• Alice"),
                new Label("• Bob"),
                new Label("• Charlie")
        );

        this.getChildren().addAll(title, searchBar, newContact, new Separator(), contactList);
    }

    public void showFilterPanel() {
        this.getChildren().clear();

        Text title = new Text("Filter Chats");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox filterOptions = new VBox(15);
        filterOptions.setPadding(new Insets(10));
        filterOptions.getChildren().addAll(
                createFilterOption("📩", "Unread"),
                createFilterOption("👥", "Contacts"),
                createFilterOption("👤", "Non-contact Groups")
        );

        this.getChildren().addAll(title, new Separator(), filterOptions);
    }

    private HBox createFilterOption(String icon, String label) {
        Label iconLabel = new Label(icon);
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 14px;");

        HBox box = new HBox(10, iconLabel, textLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-cursor: hand;");

        return box;
    }
}