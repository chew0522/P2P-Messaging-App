package RPC_Project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZZZChatUI {

    private Main app;
    private Scene scene;
    private VBox chatListVBox;
    private TextArea searchArea;
    private VBox chatDisplayPane;
    private VBox filePreviewBox;
    private ContextMenu attachMenu;
    private Button attachButton;
    private Button addFileButton;
    private Stage primaryStage;
    private StackPane overlayPane;
    private File selectedFile = null;
    private ChatClient client;
    
    public ZZZChatUI(Main app) {
        this.app = app;
        this.client = app.getActiveClient();
        createScene();
        //openChat("null");
        //loadChatMessages("");
        // Load dummy data
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void createScene() {
        BorderPane root = new BorderPane();

        // Left vertical rectangle with profile icon at top
        VBox leftPane = new VBox();
        leftPane.setPrefWidth(80);
        leftPane.setStyle("-fx-background-color: #2c3e50;");
        leftPane.setPadding(new Insets(10));
        leftPane.setAlignment(Pos.TOP_CENTER);

        ImageView profileIcon = new ImageView(new Image(getClass().getResource("/images/ProfileIcon.jpg").toExternalForm()));
        profileIcon.setFitWidth(50);
        profileIcon.setFitHeight(50);
        profileIcon.setOnMouseClicked(e -> app.showProfilePage());
        leftPane.getChildren().add(profileIcon);

        // Top toolbar with new chat, filter icons and search bar
        HBox topToolbar = new HBox(10);
        topToolbar.setPadding(new Insets(10));
        topToolbar.setStyle("-fx-background-color: #34495e;");
        topToolbar.setAlignment(Pos.CENTER_LEFT);

        /* ImageView newChatIcon = new ImageView(new Image(getClass().getResource("/images/NewChatIcon.jpg").toExternalForm()));
        newChatIcon.setFitWidth(25);
        newChatIcon.setFitHeight(25);
        ImageView filterIcon = new ImageView(new Image(getClass().getResource("/images/FilterIcon.jpg").toExternalForm()));;
        filterIcon.setFitWidth(25);
        filterIcon.setFitHeight(25);
        
        newChatIcon.setOnMouseClicked(e -> showNewChat());
        filterIcon.setOnMouseClicked(e -> showFilterOptions());

        searchArea = new TextArea();
        searchArea.setPromptText("Search chats...");
        searchArea.setPrefRowCount(1);
        searchArea.setPrefWidth(250);
        searchArea.setWrapText(true);
        searchArea.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            String filter = searchArea.getText().trim().toLowerCase();
            loadChatMessages(filter);
        });

        topToolbar.getChildren().addAll(newChatIcon, filterIcon, searchArea);

        // Chat list VBox inside scroll pane (left 1/3)
        chatListVBox = new VBox(10);
        chatListVBox.setPadding(new Insets(10));
        chatListVBox.setStyle("-fx-background-color: #ecf0f1;");

        ScrollPane scrollPane = new ScrollPane(chatListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(300);*/
       

        // Right chat display pane (2/3)
        chatDisplayPane = new VBox();
        chatDisplayPane.setPadding(new Insets(0));
        chatDisplayPane.setStyle("-fx-background-color: #ffffff;");
        chatDisplayPane.setPrefWidth(600);
        chatDisplayPane.setAlignment(Pos.TOP_LEFT);

        Text placeholder = new Text("Select a chat to view conversation.");
        placeholder.setFont(Font.font("Arial", 16));
        chatDisplayPane.getChildren().add(placeholder);

        // Center HBox combining left list and right chat view
        HBox centerContent = new HBox();
        centerContent.getChildren().addAll(chatDisplayPane);

        root.setLeft(leftPane);
        root.setTop(topToolbar);
        root.setCenter(centerContent);

        scene = new Scene(root, 900, 600);
    }

    private ImageView createIcon(String path, double width, double height) {
        Image img = new Image(path);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        iv.setPreserveRatio(true);
        return iv;
    }

    private HBox createChatMessageRow(String userIconPath, String username, String messageContent, String timestamp, int unreadCount) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(8));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        row.setOnMouseClicked(e -> openChat(username)); // Click to show chat

        ImageView userIcon = createIcon(userIconPath, 40, 40);

        VBox messageDetails = new VBox(5);
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Text userText = new Text(username);
        userText.setFont(Font.font(16));
        userText.setStyle("-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text timeText = new Text(timestamp);
        timeText.setFont(Font.font(12));
        timeText.setFill(javafx.scene.paint.Color.GRAY);

        topRow.getChildren().addAll(userText, spacer, timeText);

        Text messageSnippet = new Text(messageContent);
        messageSnippet.setFont(Font.font(14));

        messageDetails.getChildren().addAll(topRow, messageSnippet);

        Label unreadLabel = new Label();
        if (unreadCount > 0) {
            unreadLabel.setText(String.valueOf(unreadCount));
            unreadLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 4 8 4 8; -fx-background-radius: 10;");
        }

        row.getChildren().addAll(userIcon, messageDetails, unreadLabel);
        return row;
    }

    private void loadChatMessages(String filter) {
        chatListVBox.getChildren().clear();

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("/images/Friend1.jpg", "Alice", "Hey, how are you?", "10:15 AM", 2));
        messages.add(new ChatMessage("/images/Friend2.jpg", "Bob", "Did you see the news?", "9:50 AM", 0));
        messages.add(new ChatMessage("/images/Friend3.jpg", "Charlie", "Let's meet tomorrow.", "Yesterday", 1));
        messages.add(new ChatMessage("/images/Friend4.jpg", "David", "Good morning!", "Yesterday", 0));

        for (ChatMessage msg : messages) {
            if (filter.isEmpty() || msg.username.toLowerCase().contains(filter) || msg.messageContent.toLowerCase().contains(filter)) {
                chatListVBox.getChildren().add(createChatMessageRow(msg.userIconPath, msg.username, msg.messageContent, msg.timestamp, msg.unreadCount));
            }
        }
    }

    private void openChat(String username) {
    	 chatDisplayPane.getChildren().clear();

    	    // Initialize filePreviewBox here
    	    filePreviewBox = new VBox();
    	    filePreviewBox.setVisible(false);
    	    filePreviewBox.setPadding(new Insets(10));
    	    filePreviewBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 5;");
        
        // ==== TOP BAR ====
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dcdcdc;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label contactName = new Label(username);
        contactName.setFont(Font.font(18));
        contactName.setOnMouseClicked(e -> {
            showContactProfile(username);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        /* */
        ImageView searchIcon =new ImageView(new Image(getClass().getResource("/images/SerachIcon.jpg").toExternalForm()));
        searchIcon.setFitWidth(25);
        searchIcon.setFitHeight(25);
        
        searchIcon.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search");
            alert.setHeaderText("Search Chat History");
            alert.setContentText("Search functionality will be added later.");
            alert.showAndWait();
        });

        topBar.getChildren().addAll(contactName, spacer, searchIcon);
        
        // ==== CHAT HISTORY (DUMMY) ====
        VBox messagesPane = new VBox(10);
        messagesPane.setPadding(new Insets(10));
        for (int i = 1; i <= 5; i++) {
            Text message = new Text(username + ": This is message " + i);
            messagesPane.getChildren().add(message);
        }

        ScrollPane chatScrollPane = new ScrollPane(messagesPane);
        chatScrollPane.setFitToWidth(true);
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);

        // ==== BOTTOM BAR ====
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc;");
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // Icons
        ImageView emojiIcon = new ImageView(new Image(getClass().getResource("/images/EmojiIcon.jpg").toExternalForm()));
        emojiIcon.setFitWidth(25);
        emojiIcon.setFitHeight(25);

        ImageView attachIcon = new ImageView(new Image(getClass().getResource("/images/FileUploadIcon.jpg").toExternalForm()));
        attachIcon.setFitWidth(25);
        attachIcon.setFitHeight(25);

        // Message input
        TextField messageInput = new TextField();
        messageInput.setPromptText("Type a message...");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        // Emoji picker button (using your emoji icon)
        Button emojiButton = new Button();
        emojiButton.setGraphic(emojiIcon);
        emojiButton.setOnAction(e -> 
        	showEmojiPicker(messageInput));

        // Attach button (optional: add action to attach files)
        attachButton = new Button("📎");
        attachButton.setFont(Font.font(18));
        attachButton.setFocusTraversable(false);

        attachMenu = new ContextMenu();
        
        // Create Popup (ContextMenu style)
        MenuItem photosVideos = new MenuItem("📷 Photos & Videos");
        MenuItem documents = new MenuItem("📄 Document");

        attachMenu.getItems().addAll(photosVideos, documents);
       
        // Show popup when button clicked
        attachButton.setOnAction(e -> {
            if (!attachMenu.isShowing()) {
                attachMenu.show(attachButton, Side.TOP, 0, 0);
            } else {
                attachMenu.hide();
            }
        });

        // Example actions
        photosVideos.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Photos & Videos");

            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                    "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
            FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter(
                    "Video Files", "*.mp4", "*.avi", "*.mov", "*.mkv");
            fileChooser.getExtensionFilters().addAll(imageFilter, videoFilter);

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    addFilePreview(selectedFile);
                    filePreviewBox.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
       
        documents.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Document");

            // Set extension filters for common document types
            FileChooser.ExtensionFilter docFilter = new FileChooser.ExtensionFilter(
                    "Document Files", "*.pdf", "*.doc", "*.docx", "*.txt", "*.xls", "*.xlsx");
            fileChooser.getExtensionFilters().add(docFilter);

            File selectedFile = fileChooser.showOpenDialog(attachMenu);
            if (selectedFile != null) {
                System.out.println("Selected document: " + selectedFile.getAbsolutePath());
                // Do something with the selected file here
            }
        });
        
        
        // Send button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String textMessage = messageInput.getText().trim();
            String caption = ""; // TODO: get the caption from the caption field in your file preview UI
            File attachedFile = selectedFile; // Make sure selectedFile is a class field updated when you attach file

            if (!textMessage.isEmpty() || attachedFile != null) {
                // Show message text
            	addMessage("You", textMessage, selectedFile, caption);
                messageInput.clear();
                filePreviewBox.getChildren().clear();
                filePreviewBox.setVisible(false);
                selectedFile = null;

                // Show attached file + caption if present
                if (attachedFile != null) {
                    HBox fileMessage = new HBox(5);
                    ImageView fileIcon = new ImageView(getFileIcon(attachedFile));
                    fileIcon.setFitWidth(20);
                    fileIcon.setFitHeight(20);
                    Text fileName = new Text(attachedFile.getName());
                    fileMessage.getChildren().addAll(fileIcon, fileName);

                    if (!caption.isEmpty()) {
                        Text captionText = new Text("Caption: " + caption);
                        fileMessage.getChildren().add(captionText);
                    }
                    messagesPane.getChildren().add(fileMessage);
                }

                // Clear input and preview
                messageInput.clear();
                filePreviewBox.getChildren().clear();
                filePreviewBox.setVisible(false);
                selectedFile = null;
            }
        });

        // Add all to bottom bar
        bottomBar.getChildren().addAll(emojiButton, attachButton, messageInput, sendButton);
        
        chatDisplayPane.getChildren().addAll(topBar, chatScrollPane, bottomBar, filePreviewBox);
       
        filePreviewBox = new VBox();
        filePreviewBox.setVisible(false);
        filePreviewBox.setPadding(new Insets(10));
        filePreviewBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 5;");
        

        StackPane overlayPane = new StackPane();
        overlayPane.getChildren().addAll(chatDisplayPane, filePreviewBox);
        
        Scene scene = new Scene(overlayPane, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addFilePreview(File file) throws IOException {
        HBox filePreview = new HBox(10);
        filePreview.setPadding(new Insets(10));
        filePreview.setStyle("-fx-border-color: #ccc; -fx-background-color: #fff; -fx-border-radius: 5;");
        filePreview.setAlignment(Pos.CENTER_LEFT);

        // File icon based on type
        ImageView fileIcon = new ImageView(getFileIcon(file)); // Implement getFileIcon()
        fileIcon.setFitWidth(40);
        fileIcon.setFitHeight(40);

        // File info VBox
        VBox fileInfo = new VBox(5);
        Text fileName = new Text(file.getName());
        Text fileSize = new Text(humanReadableByteCountSI(file.length())); // Implement humanReadableByteCountSI()
        Text fileType = new Text(getFileType(file)); // Implement getFileType()

        fileInfo.getChildren().addAll(fileName, fileSize, fileType);

        // Caption with emoji picker
        TextField captionField = new TextField();
        captionField.setPromptText("Add caption (optional)");
        captionField.setPrefWidth(200);

        Button emojiButton = new Button();
        emojiButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/emoji_icon.png"), 20, 20, true, true)));
        emojiButton.setOnAction(e -> showEmojiPicker(captionField)); // Implement showEmojiPicker()

        HBox captionBox = new HBox(5, captionField, emojiButton);
        captionBox.setAlignment(Pos.CENTER_LEFT);

        // Add icon to add more files
        Button addFileButton = new Button();
        addFileButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/add_icon.png"), 20, 20, true, true)));
        addFileButton.setOnAction(e -> openFileExplorer()); // Implement openFileExplorer()

        // Add everything
        VBox filePreviewContent = new VBox(5, fileInfo, captionBox);
        filePreview.getChildren().addAll(fileIcon, filePreviewContent, addFileButton);

        filePreviewBox.getChildren().add(filePreview);
        filePreviewBox.setVisible(true);
    }
    
    private Object openFileExplorer() {
		// TODO Auto-generated method stub
		return null;
	}

	private Image getFileIcon(File file) {
        String name = file.getName().toLowerCase();
        String iconPath = "/icons/file_generic.png"; // default icon

        if (name.endsWith(".pdf")) {
            iconPath = "/icons/pdf_icon.png";
        } else if (name.endsWith(".doc") || name.endsWith(".docx")) {
            iconPath = "/icons/word_icon.png";
        } else if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            iconPath = "/icons/excel_icon.png";
        } else if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) {
            iconPath = "/icons/image_icon.png";
        }
        // add more cases as needed

        return new Image(getClass().getResourceAsStream(iconPath));
    }

    private String getFileType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) return "PDF Document";
        if (name.endsWith(".doc") || name.endsWith(".docx")) return "Word Document";
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) return "Excel Spreadsheet";
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) return "Image File";
        return "File";
    }
    
    private String humanReadableByteCountSI(long bytes) {
        int unit = 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "kMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private void showFilePreview(File file) throws IOException {
        // Clear previous preview
        filePreviewBox.getChildren().clear();

        // File details
        String fileName = file.getName();
        long fileSizeBytes = file.length();
        String fileSize = String.format("%.2f KB", fileSizeBytes / 1024.0);
        String fileType = Files.probeContentType(file.toPath());

        // Icon (based on extension)
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String icon = switch (extension) {
            case "pdf" -> "📄";
            case "doc", "docx" -> "📝";
            case "xls", "xlsx" -> "📊";
            case "png", "jpg", "jpeg", "gif" -> "🖼️";
            case "mp4", "avi", "mov", "mkv" -> "🎬";
            default -> "📁";
        };

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 40px;");

        VBox fileInfo = new VBox(
            new Label("Name: " + fileName),
            new Label("Size: " + fileSize),
            new Label("Type: " + (fileType != null ? fileType : "Unknown"))
        );
        fileInfo.setSpacing(5);

        HBox preview = new HBox(iconLabel, fileInfo);
        preview.setSpacing(15);
        preview.setAlignment(Pos.CENTER_LEFT);

        // Emoji + caption box
        HBox captionBox = new HBox();
        Label emojiLabel = new Label("😊");
        TextField captionField = new TextField();
        captionField.setPromptText("Write a caption...");
        captionBox.getChildren().addAll(emojiLabel, captionField);
        captionBox.setSpacing(10);
        captionBox.setAlignment(Pos.CENTER_LEFT);

        // Add more files button
        Button addMoreButton = new Button("➕ Add More Files");
        addMoreButton.setOnAction(e -> {
            attachMenu.show(attachButton, Side.TOP, 0, 0); // Reopen file selector
        });

        filePreviewBox.getChildren().addAll(preview, captionBox, addMoreButton);
        filePreviewBox.setSpacing(10);
        filePreviewBox.setVisible(true);
    }

    // Show profile in right pane without scene switch
    private void showProfile() {
        chatDisplayPane.getChildren().clear();

        VBox profilePane = new VBox(20);
        profilePane.setPadding(new Insets(20));
        profilePane.setAlignment(Pos.TOP_CENTER);

        ImageView userPhoto = new ImageView(new Image(getClass().getResourceAsStream("/icons/user_photo.jpg")));
        userPhoto.setFitWidth(120);
        userPhoto.setFitHeight(120);
        userPhoto.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0); -fx-background-radius: 60;");

        Text usernameText = new Text("Raymond Ng");
        usernameText.setFont(Font.font(24));
        Text phoneText = new Text("01128084108");
        phoneText.setFont(Font.font(16));
        phoneText.setStyle("-fx-text-fill: gray;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(150);
        logoutBtn.setOnAction(e -> app.showLoginPage());

        profilePane.getChildren().addAll(userPhoto, usernameText, phoneText, logoutBtn);
        chatDisplayPane.getChildren().add(profilePane);
    }

    // Show "New Chat" UI in right pane
 // Show "New Chat" UI in right pane
    private void showNewChat() {
        chatDisplayPane.getChildren().clear();

        VBox newChatPane = new VBox(15);
        newChatPane.setPadding(new Insets(20));
        newChatPane.setAlignment(Pos.TOP_LEFT);

        Label label = new Label("New Chat");
        label.setFont(Font.font(20));

        TextField searchUser = new TextField();
        searchUser.setPromptText("Search username or number");

        Label newContactLabel = new Label("New Contact");
        newContactLabel.setFont(Font.font(16));
        newContactLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        VBox contactList = new VBox(8);
        contactList.setPadding(new Insets(0, 0, 0, 10));

        List<String> contacts = Arrays.asList("Alice Johnson", "Bob Smith", "Charlie Brown", "David Lee");

        // Function to update contact list based on search
        Runnable updateContactList = () -> {
            String filter = searchUser.getText().toLowerCase().trim();
            contactList.getChildren().clear();

            for (String name : contacts) {
                if (name.toLowerCase().contains(filter)) {
                    Label contactLabel = new Label(name);
                    contactLabel.setStyle("-fx-cursor: hand; -fx-text-fill: #3366cc;");
                    contactLabel.setOnMouseClicked(e -> {
                        searchUser.setText(name);
                    });
                    contactList.getChildren().add(contactLabel);
                }
            }

            if (contactList.getChildren().isEmpty()) {
                contactList.getChildren().add(new Label("No contacts found"));
            }
        };

        // Update contact list as user types
        searchUser.textProperty().addListener((obs, oldVal, newVal) -> updateContactList.run());

        // Initial list population
        updateContactList.run();

        Button startChatBtn = new Button("Start Chat");
        startChatBtn.setOnAction(e -> {
            String username = searchUser.getText().trim();
            if (!username.isEmpty()) {
                openChat(username);
            }
        });

        newChatPane.getChildren().addAll(label, searchUser, newContactLabel, contactList, startChatBtn);
        chatDisplayPane.getChildren().add(newChatPane);
    }
    
    private void addMessage(String sender, String text, File attachment, String caption) {
        VBox messageBox = new VBox(5);
        VBox messagesPane = new VBox(10);
        messageBox.setPadding(new Insets(5));
        messageBox.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 5; -fx-background-radius: 10;");

        Text senderText = new Text(sender);
        senderText.setStyle("-fx-font-weight: bold;");

        Text messageText = new Text(text);
        
        messageBox.getChildren().addAll(senderText, messageText);

        if (attachment != null) {
            HBox fileBox = new HBox(5);
            ImageView fileIcon = new ImageView(getFileIcon(attachment));
            fileIcon.setFitWidth(30);
            fileIcon.setFitHeight(30);
            Label fileName = new Label(attachment.getName());
            fileBox.getChildren().addAll(fileIcon, fileName);
            messageBox.getChildren().add(fileBox);

            if (!caption.isEmpty()) {
                Label captionLabel = new Label("Caption: " + caption);
                messageBox.getChildren().add(captionLabel);
            }
        }

        // Add this message box to your main messagesPane UI container
        messagesPane.getChildren().add(messageBox);
    }



    // Show filter options in right pane
    private void showFilterOptions() {
        chatDisplayPane.getChildren().clear();

        VBox filterPane = new VBox(15);
        filterPane.setPadding(new Insets(20));
        filterPane.setAlignment(Pos.TOP_LEFT);

        Label label = new Label("Filter Options");
        label.setFont(Font.font(20));

        // Each filter item with icon, label and checkbox
        HBox unreadFilter = createFilterItemWithCheckbox("/icons/unread_icon.png", "Unread");
        HBox contactsFilter = createFilterItemWithCheckbox("/icons/contacts_icon.png", "Contacts");
        HBox nonContactGroupsFilter = createFilterItemWithCheckbox("/icons/groups_icon.png", "Non-contact Groups");


        Button applyFilterBtn = new Button("Apply Filter");
        applyFilterBtn.setOnAction(e -> {
            // You can add logic here to read which checkboxes are selected and filter accordingly
            loadChatMessages(searchArea.getText().trim().toLowerCase());
        });

        filterPane.getChildren().addAll(label, unreadFilter, contactsFilter, nonContactGroupsFilter, applyFilterBtn);
        chatDisplayPane.getChildren().add(filterPane);
    }

    // Helper method to create filter item with icon, text and a checkbox
    private HBox createFilterItemWithCheckbox(String iconPath, String text) {
        ImageView icon = createIcon(iconPath, 25, 25);
        Label label = new Label(text);
        label.setFont(Font.font(16));
        CheckBox checkBox = new CheckBox();
        HBox box = new HBox(10, checkBox, icon, label);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5));
        box.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f7f7f7;");
        return box;
    }
    
    private void showEmojiPicker(TextField messageInput) {
        Stage emojiStage = new Stage();
        emojiStage.setTitle("Pick Emoji");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Search emoji...");

        TabPane tabPane = new TabPane();

        // Example categories
        String[] smileys = {"😀", "😂", "😍", "😎", "😢", "😡"};
        String[] animals = {"🐶", "🐱", "🦁", "🐼", "🐸"};
        // ... add more categories

        tabPane.getTabs().addAll(
            createEmojiTab("Smileys", smileys, messageInput),
            createEmojiTab("Animals", animals, messageInput)
            // Add other tabs here
        );

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            for (Tab tab : tabPane.getTabs()) {
                VBox content = (VBox) tab.getContent();
                GridPane grid = (GridPane) content.getChildren().get(1);
                filterEmojiGrid(grid, newText);
            }
        });

        root.getChildren().addAll(searchField, tabPane);

        Scene scene = new Scene(root, 400, 300);
        emojiStage.setScene(scene);
        emojiStage.initModality(Modality.APPLICATION_MODAL);
        emojiStage.show();
    }

    private Tab createEmojiTab(String categoryName, String[] emojis, TextField messageInput) {
        Tab tab = new Tab(categoryName);

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));

        // Label or title for emoji category (optional)
        Label categoryLabel = new Label(categoryName);
        categoryLabel.setFont(Font.font(16));

        // Grid to hold emoji buttons
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int cols = 6;  // number of emojis per row
        for (int i = 0; i < emojis.length; i++) {
            String emoji = emojis[i];
            Button emojiBtn = new Button(emoji);
            emojiBtn.setFont(Font.font(24));
            emojiBtn.setStyle("-fx-background-color: transparent;");
            emojiBtn.setOnAction(e -> {
                // Insert selected emoji at the current cursor position in message input
                int pos = messageInput.getCaretPosition();
                String oldText = messageInput.getText();
                String newText = oldText.substring(0, pos) + emoji + oldText.substring(pos);
                messageInput.setText(newText);
                messageInput.positionCaret(pos + emoji.length());
            });

            grid.add(emojiBtn, i % cols, i / cols);
        }

        vbox.getChildren().addAll(categoryLabel, grid);
        tab.setContent(vbox);

        return tab;
    }

    private void filterEmojiGrid(GridPane grid, String filter) {
        // Simple filter: hide emoji buttons if their text does not contain the filter
        filter = filter.toLowerCase();
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                String emojiText = btn.getText();
                if (emojiText.toLowerCase().contains(filter) || filter.isEmpty()) {
                    btn.setVisible(true);
                    btn.setManaged(true);
                } else {
                    btn.setVisible(false);
                    btn.setManaged(false);
                }
            }
        }
    }
    
    
    private void showAttachmentMenu(Node anchorNode) {
        VBox attachmentMenu = new VBox(10);
        attachmentMenu.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");

        String[] options = {"Photos & videos", "Camera", "Document", "Contact", "Poll", "Drawing"};
        String[] icons = {"📷", "📸", "📄", "👤", "📊", "✏️"};

        Popup popup = new Popup();  // declare and assign before loop

        for (int i = 0; i < options.length; i++) {
            String option = options[i]; // capture current option safely
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(icons[i]);
            icon.setStyle("-fx-font-size: 16px;");
            Label label = new Label(option);
            label.setStyle("-fx-font-size: 14px;");
            row.getChildren().addAll(icon, label);
            row.setOnMouseClicked(e -> {
                System.out.println("Selected: " + option);  // use captured option
                popup.hide();  // popup is accessible here because it's final in context
            });
            attachmentMenu.getChildren().add(row);
        }

        popup.getContent().add(attachmentMenu);
        popup.setAutoHide(true);
        popup.show(anchorNode.getScene().getWindow(), 
                   anchorNode.localToScreen(0, 0).getX(), 
                   anchorNode.localToScreen(0, 0).getY() - 150);
    }

    private void showContactProfile(String contactName) {
        Stage profileStage = new Stage();
        profileStage.setTitle("Contact Info - " + contactName);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Profile Photo (Placeholder)
        ImageView profilePic = new ImageView(new Image(getClass().getResourceAsStream("/icons/default_profile.png")));
        profilePic.setFitHeight(100);
        profilePic.setFitWidth(100);
        profilePic.setPreserveRatio(true);

        // Username
        Label nameLabel = new Label(contactName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Phone Number (Placeholder)
        Label phoneLabel = new Label("Phone: +6012-345-6789");

        // Mute Notifications Toggle
        HBox muteBox = new HBox(10);
        muteBox.setAlignment(Pos.CENTER_LEFT);
        Label muteLabel = new Label("Mute Notifications:");
        ChoiceBox<String> muteChoice = new ChoiceBox<>();
        muteChoice.getItems().addAll("Yes", "No");
        muteChoice.setValue("No");
        muteBox.getChildren().addAll(muteLabel, muteChoice);

        // Block and Report Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        Button blockBtn = new Button("Block");
        Button reportBtn = new Button("Report Contact");
        buttonBox.getChildren().addAll(blockBtn, reportBtn);

        // TabPane for Media, Files, Links
        TabPane tabPane = new TabPane();

        Tab mediaTab = new Tab("Media");
        mediaTab.setContent(new Label("All shared images/videos go here"));

        Tab fileTab = new Tab("Files");
        fileTab.setContent(new Label("All shared files go here"));

        Tab linksTab = new Tab("Links");
        linksTab.setContent(new Label("All shared links go here"));

        tabPane.getTabs().addAll(mediaTab, fileTab, linksTab);

        root.getChildren().addAll(profilePic, nameLabel, phoneLabel, muteBox, buttonBox, tabPane);

       

        Scene scene = new Scene(root, 400, 500);
        profileStage.setScene(scene);
        profileStage.initModality(Modality.APPLICATION_MODAL);
        profileStage.show();
    }

    public Scene getScene() {
        return scene;
    }

    // Dummy inner class for chat message data
    private static class ChatMessage {
        String userIconPath;
        String username;
        String messageContent;
        String timestamp;
        int unreadCount;

        public ChatMessage(String userIconPath, String username, String messageContent, String timestamp, int unreadCount) {
            this.userIconPath = userIconPath;
            this.username = username;
            this.messageContent = messageContent;
            this.timestamp = timestamp;
            this.unreadCount = unreadCount;
        }
    }
    
    
}