package RPC_Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class ChatUI {
    
    private Main app;
    private Scene scene;
    private PeerClientClass server;
    private PeerClientClass client;
    private DatabaseManager dbManager;
    private Stage primaryStage;
    private ImageView attachFileIcon;
    private VBox filePreviewBox;
    private File selectedFile = null;
    private VBox messagesPane;
    private BorderPane root;
    private Popup filePreviewPopup;
    private ScrollPane chatScrollPane;
    private User sender;
    private User receiver;

    public ChatUI(Main app){
        this.app = app;
        this.server = app.getActiveServer();
        this.client = app.getActiveClient();
        this.dbManager = app.getDatabaseManager();
        this.sender = app.getSender();
        this.receiver = app.getReceiver();
        createScene();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void createScene(){
        root = new BorderPane();

        // Left vertical rectangle with profile icon at top
        VBox leftPane = new VBox();
        leftPane.setPrefWidth(80);
        leftPane.setStyle("-fx-background-color: #1e2d3b;");
        leftPane.setPadding(new Insets(10));
        leftPane.setAlignment(Pos.TOP_CENTER);

        ImageView profileIcon = new ImageView(new Image(getClass().getResource("/images/ProfileIcon.png").toExternalForm()));
        profileIcon.setFitWidth(50);
        profileIcon.setFitHeight(50);
        profileIcon.setOnMouseClicked(e -> app.showProfilePage());
        leftPane.getChildren().add(profileIcon);

        Label chatLabel = new Label(receiver.getUsername());
        chatLabel.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-weight: bold;"  + 
            "-fx-font-size: 20px;"
        );
        HBox topToolbar = new HBox(10);
        topToolbar.setPadding(new Insets(10));
        topToolbar.setStyle("-fx-background-color: #1e2d3b;");
        topToolbar.setAlignment(Pos.CENTER);
        topToolbar.getChildren().add(chatLabel);

        root.setLeft(leftPane);
        root.setTop(topToolbar);
        setMessagePane();
        loadChatHistory(dbManager.getConnection(), sender.getUserID(), receiver.getUserID());
        startReceiving("received_" + server.getFileName());

        scene = new Scene(root, 1000, 600);
    }

    public void setMessagePane(){
        BorderPane chatUIBox = new BorderPane();
        messagesPane = new VBox();
        messagesPane.setPadding(new Insets(10));
        messagesPane.setStyle("-fx-background-color: #2b3947");
        messagesPane.setFillWidth(true);

        chatScrollPane = new ScrollPane(messagesPane);
        chatScrollPane.setStyle("-fx-background: #2b3947;");
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(false);

        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);

        // ==== BOTTOM BAR ====
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setStyle("-fx-background-color: #1e2d3b;");
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // EmojiIcon
        ImageView emojiIcon = new ImageView(new Image(getClass().getResource("/images/EmojiIcon.png").toExternalForm()));
        emojiIcon.setFitWidth(40);
        emojiIcon.setFitHeight(40);

        // Message input
        TextField messageInput = new TextField();
        messageInput.setPromptText("Type a message...");
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        // Emoji picker button (using your emoji icon)
        emojiIcon.setOnMouseClicked(e -> showEmojiPopup(messageInput, emojiIcon));


        // EmojiIcon
        attachFileIcon = new ImageView(new Image(getClass().getResource("/images/FileUploadIcon.png").toExternalForm()));
        attachFileIcon.setFitWidth(40);
        attachFileIcon.setFitHeight(40);
        attachFileIcon.setOnMouseClicked(e -> openFileExplorer());

        // Send button
        ImageView sendButton = new ImageView(new Image(getClass().getResource("/images/SendButton.png").toExternalForm()));
        sendButton.setFitWidth(40);
        sendButton.setFitHeight(40);
        sendButton.setOnMouseClicked(e -> {
            String textMessage = messageInput.getText().trim();
            try {
                dbManager.insertTextMessage(sender.getUserID(), receiver.getUserID(), textMessage);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            addMessage(textMessage);

            try {
                client.sendText(textMessage);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // Clear input and preview
            messageInput.clear();
            filePreviewBox.getChildren().clear();
            filePreviewBox.setVisible(false);
            selectedFile = null;
            
        });

        // Add all to bottom bar
        bottomBar.getChildren().addAll(emojiIcon, attachFileIcon, messageInput, sendButton);       
        filePreviewBox = new VBox();
        filePreviewBox.setVisible(false);
        filePreviewBox.setPadding(new Insets(10));
        filePreviewBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 5;");

        filePreviewPopup = new Popup();
        filePreviewPopup.getContent().add(filePreviewBox);
        // Optional: Set auto-hide so clicking outside dismisses it
        filePreviewPopup.setAutoHide(true);
        // Optional: Hide on focus lost to also dismiss it
        filePreviewPopup.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && filePreviewPopup.isShowing()) {
                // filePreviewPopup.hide(); // Uncomment if you want it to hide when focus leaves the popup
            }
        });

        chatUIBox.setCenter(chatScrollPane);
        chatUIBox.setBottom(bottomBar);
        root.setCenter(chatUIBox);
        
    }

    private void showEmojiPopup(TextField messageInput, Node anchorNode) {
        Popup emojiPopup = new Popup();
        emojiPopup.setAutoHide(true);
        emojiPopup.setAutoFix(true);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

        TabPane tabPane = new TabPane();

        String[] smileys = {
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙", "😚", "😋",
            "😜", "😝", "😛", "🤑", "🤗", "🤔", "😐", "😑", "😶",
            "🙄", "😏", "😣", "😥", "😮", "🤐", "😯", "😪", "😫",
            "😴", "😌", "😓", "😔", "😕", "🙁", "😢", "😭", "😤",
            "😠", "😡", "😳", "😱", "😨", "😰", "😥"
        };

        String[] animals = {
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
            "🦁", "🐮", "🐷", "🐸", "🐵", "🦍", "🐔", "🐧", "🐦", "🐤",
            "🦆", "🦅", "🦉", "🦇", "🐺", "🦄", "🐝", "🦂"
        };

        String[] food = {
            "🍎", "🍌", "🍇", "🍓", "🍒", "🍍", "🍉", "🥝", "🍑",
            "🍅", "🥑", "🍆", "🥕", "🌽", "🥔", "🍠", "🍞", "🥐",
            "🥖", "🧀", "🍗", "🍖", "🍔", "🍟", "🍕", "🌭",
            "🍿", "🍩", "🍪", "🎂", "🍰", "🍫", "🍬", "🍭"
        };

        String[] nature = {
            "🌸", "🌼", "🌻", "🌺", "🌷", "🌹", "🥀", "🌳", "🌴", "🌵",
            "🍀", "🌾", "🌿", "🍁", "🍂", "🍃", "🌍", "🌎", "🌏", "🌕",
            "🌑", "🌙", "⭐", "✨", "⚡", "⛅", "🌈"
        };

        String[] symbols = {
            "💛", "💚", "💙", "💜", "🖤", "💔",
            "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "✅", "❌",
            "❗", "❓", "🔒", "🔓", "🔑", "🔔", "🔕", "💡"
        };

        tabPane.getTabs().addAll(
            createEmojiTab("Smileys", smileys, messageInput),
            createEmojiTab("Animals", animals, messageInput), 
            createEmojiTab("Foods", food, messageInput), 
            createEmojiTab("Nature", nature, messageInput), 
            createEmojiTab("Symbols", symbols, messageInput)
        );


        root.getChildren().addAll(tabPane);

        emojiPopup.getContent().add(root);

        // Position popup below the anchorNode
        emojiPopup.show(
            primaryStage, // Owner window (needed for correct stacking and behavior)
            anchorNode.localToScreen(0, 0).getX(), // X coordinate
            anchorNode.localToScreen(0, 0).getY() - 325 // Y coordinate (above the button, with padding)
        );

    }

    private Tab createEmojiTab(String categoryName, String[] emojis, TextField messageInput) {
        Tab tab = new Tab(categoryName);
        tab.setClosable(false);

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));

        // Label or title for emoji category
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
            emojiBtn.setFont(Font.font(18));
            emojiBtn.setStyle("-fx-background-color: transparent;");
            emojiBtn.setOnAction(e -> {
                int pos = messageInput.getCaretPosition();
                String oldText = messageInput.getText();
                String newText = oldText.substring(0, pos) + emoji + oldText.substring(pos);
                messageInput.setText(newText);
                messageInput.positionCaret(pos + emoji.length());
            });

            grid.add(emojiBtn, i % cols, i / cols);
        }

        // Make the grid scrollable without resizing the tab
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);  // Adjust height as needed
        scrollPane.setPrefViewportWidth(350);   // Adjust width as needed
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color:transparent;");

        vbox.getChildren().addAll(categoryLabel, scrollPane);
        tab.setContent(vbox);

        return tab;
    }

    private void openFileExplorer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(new Stage()); // You need a Stage here
        if (selectedFile != null) {
            this.selectedFile = selectedFile;
            showFilePreview(selectedFile);
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            // Do something with the selected file, e.g., load its content
        }
    }

    private String humanReadableByteCountSI(long bytes) {
        int unit = 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "kMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private void showFilePreview(File file) {
        // Clear previous preview
        filePreviewBox.getChildren().clear();
        filePreviewBox.setStyle("-fx-background-color: #1e2d3b;");

        // File details
        String fileName = file.getName();
        long fileSizeBytes = file.length();
        String fileSize = humanReadableByteCountSI(fileSizeBytes);
        String fileType;
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            System.err.println("Error probing file type for " + fileName + ": " + e.getMessage());
            fileType = "Unknown (Error)"; // Assign a default in case of error
        }

        String extension = "";
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        String imagePath = switch (extension.toLowerCase()) {
            case "pdf" -> "/images/PDF.png";
            case "doc", "docx" -> "/images/DOCX.png";
            case "xls", "xlsx" -> "/images/XLSX.png";
            case "png", "jpg", "jpeg", "gif" -> "/images/IMAGE.png";
            case "mp4", "avi", "mov", "mkv" -> "/images/VIDEO.png";
            default -> "/images/FOLDER.png";
        };

        ImageView iconLabel = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        iconLabel.setFitWidth(40);
        iconLabel.setFitHeight(40);

        // File info
        Label nameLabel = new Label(fileName);
        Label sizeLabel = new Label(fileSize);
        Label typeLabel = new Label(fileType != null ? fileType : "Unknown");

        // Set text color to white
        nameLabel.setStyle("-fx-text-fill: white;");
        sizeLabel.setStyle("-fx-text-fill: white;");
        typeLabel.setStyle("-fx-text-fill: white;");

        VBox fileInfo = new VBox(nameLabel, sizeLabel, typeLabel);

        fileInfo.setSpacing(5);

        // Send button
        ImageView sendButton = new ImageView(new Image(getClass().getResource("/images/SendButton.png").toExternalForm()));
        sendButton.setFitWidth(40);
        sendButton.setFitHeight(40);
        sendButton.setOnMouseClicked(e -> {
            addFileBox(file);
            try {
                client.sendFile(file.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            filePreviewBox.getChildren().clear();
            filePreviewBox.setVisible(false);
            selectedFile = null;
        });

        // BorderPane layout
        BorderPane preview = new BorderPane();
        // Wrap iconLabel in a VBox with top padding
        VBox iconWrapper = new VBox(iconLabel);
        iconWrapper.setPadding(new Insets(8, 5, 0, 0)); // top, right, bottom, left

        // Wrap sendButton in a VBox with top padding
        VBox buttonWrapper = new VBox(sendButton);
        buttonWrapper.setPadding(new Insets(8, 0, 0, 5));

        // Apply to BorderPane
        preview.setLeft(iconWrapper);
        preview.setCenter(fileInfo);
        preview.setRight(buttonWrapper);
        BorderPane.setMargin(iconLabel, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(fileInfo, new Insets(0, 10, 0, 0));
        preview.setPadding(new Insets(5));

        // Finalize
        filePreviewBox.getChildren().add(preview);
        filePreviewBox.setSpacing(10);
        filePreviewBox.setVisible(true);
        filePreviewPopup.show(
            primaryStage, // Owner window (needed for correct stacking and behavior)
            attachFileIcon.localToScreen(0, 0).getX(), // X coordinate
            attachFileIcon.localToScreen(0, 0).getY() - filePreviewBox.prefHeight(-1) - 50 // Y coordinate (above the button, with padding)
        );
    }

    private void addMessage(String text) {
        
        VBox messageBox = new VBox(5);
        Text messageText = new Text(text);
        messageText.setWrappingWidth(300);
        messageBox.getChildren().add(messageText);


        messageBox.setPadding(new Insets(10));
        messageBox.setMaxWidth(600);
        messageBox.setStyle(
            "-fx-background-color: #DCF8C6;" + // Light green (like WhatsApp sender)
            "-fx-background-radius: 16 16 4 16;" + // top-left, top-right, bottom-right, bottom-left
            "-fx-border-radius: 16 16 4 16;" +
            "-fx-border-color: #A5D6A7;" + // Optional border
            "-fx-border-width: 1;"
        );

        HBox wrapper = new HBox(messageBox);
        wrapper.setAlignment(Pos.CENTER_RIGHT); // Push to right side
        wrapper.setPadding(new Insets(5, 10, 5, 10)); // Add spacing

        messagesPane.getChildren().add(wrapper);

        // Scroll to bottom after layout update
        messagesPane.layout();
        Platform.runLater(() -> {
            messagesPane.layout(); // Ensure layout is refreshed
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0)); // Scroll after layout
        });
    }

    private void addReceivedMessage(String text) {
        
        VBox messageBox = new VBox(5);
        Text messageText = new Text(text);
        messageText.setWrappingWidth(300);
        messageBox.getChildren().add(messageText);


        messageBox.setPadding(new Insets(10));
        messageBox.setMaxWidth(600);
        messageBox.setStyle(
            "-fx-background-color: #ced2d5;" + // Light green (like WhatsApp sender)
            "-fx-background-radius: 16 16 16 4;" + // top-left, top-right, bottom-right, bottom-left
            "-fx-border-radius: 16 16 16 4;" +
            "-fx-border-width: 1;"
        );

        HBox wrapper = new HBox(messageBox);
        wrapper.setAlignment(Pos.CENTER_LEFT); // Push to right side
        wrapper.setPadding(new Insets(5, 10, 5, 10)); // Add spacing

        messagesPane.getChildren().add(wrapper);

        // Scroll to bottom after layout update
        messagesPane.layout();
        Platform.runLater(() -> {
            messagesPane.layout(); // Ensure layout is refreshed
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0)); // Scroll after layout
        });
    }


    private void addFileBox(File file) {
        String fileName = file.getName();
        long fileSizeBytes = file.length();
        String fileSize = humanReadableByteCountSI(fileSizeBytes);
        String fileType;
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            System.err.println("Error probing file type for " + fileName + ": " + e.getMessage());
            fileType = "Unknown";
        }

        String extension = "";
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        String imagePath = switch (extension) {
            case "pdf" -> "/images/PDF.png";
            case "doc", "docx" -> "/images/DOCX.png";
            case "xls", "xlsx" -> "/images/XLSX.png";
            case "png", "jpg", "jpeg", "gif" -> "/images/IMAGE.png";
            case "mp4", "avi", "mov", "mkv" -> "/images/VIDEO.png";
            default -> "/images/FOLDER.png";
        };

        ImageView iconLabel = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        iconLabel.setFitWidth(40);
        iconLabel.setFitHeight(40);

        Label nameLabel = new Label(fileName);
        nameLabel.setStyle("-fx-text-fill: white;");

        Label sizeLabel = new Label(fileSize);
        sizeLabel.setStyle("-fx-text-fill: white;");

        Label typeLabel = new Label(fileType);
        typeLabel.setStyle("-fx-text-fill: white;");

        VBox fileInfo = new VBox(nameLabel, sizeLabel, typeLabel);
        fileInfo.setSpacing(5);

        // Send button (download)
        ImageView downloadButton = new ImageView(new Image(getClass().getResource("/images/DownloadButton.png").toExternalForm()));
        downloadButton.setFitWidth(40);
        downloadButton.setFitHeight(40);
        downloadButton.setOnMouseClicked(e -> {
            Stage ownerStage = (Stage) downloadButton.getScene().getWindow();
            if (ownerStage == null) ownerStage = new Stage();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName(file.getName());

            File destinationFile = fileChooser.showSaveDialog(ownerStage);
            if (destinationFile != null) {
                try {
                    Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File downloaded to: " + destinationFile.getAbsolutePath());
                } catch (IOException ex) {
                    System.err.println("Download failed: " + ex.getMessage());
                }
            } else {
                System.out.println("Download cancelled.");
            }
        });

        // Layout setup using BorderPane
        VBox iconWrapper = new VBox(iconLabel);
        iconWrapper.setPadding(new Insets(8, 5, 0, 0));

        VBox buttonWrapper = new VBox(downloadButton);
        buttonWrapper.setPadding(new Insets(8, 0, 0, 5));

        BorderPane fileBP = new BorderPane();
        fileBP.setLeft(iconWrapper);
        fileBP.setCenter(fileInfo);
        fileBP.setRight(buttonWrapper);
        fileBP.setPadding(new Insets(5));

        // Styling the file message box
        VBox fileBox = new VBox(fileBP);
        fileBox.setSpacing(5);
        fileBox.setPadding(new Insets(10));
        fileBox.setMaxWidth(400);
        fileBox.setStyle(
            "-fx-background-color: #2b3947;" + // darker WhatsApp-like
            "-fx-background-radius: 16 16 4 16;" +
            "-fx-border-radius: 16 16 4 16;" +
            "-fx-border-color: #A5D6A7;" +
            "-fx-border-width: 1;"
        );

        HBox wrapper = new HBox(fileBox);
        wrapper.setAlignment(Pos.CENTER_RIGHT);
        wrapper.setPadding(new Insets(5, 10, 5, 10));

        messagesPane.getChildren().add(wrapper);
        Platform.runLater(() -> {
            messagesPane.layout();
            chatScrollPane.setVvalue(1.0); // scroll to bottom
        });
    }


    private void addReceivedFileBox(File file) {
        String fileName = file.getName();
        long fileSizeBytes = file.length();
        String fileSize = humanReadableByteCountSI(fileSizeBytes);
        String fileType;
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            System.err.println("Error probing file type for " + fileName + ": " + e.getMessage());
            fileType = "Unknown";
        }

        String extension = "";
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        String imagePath = switch (extension) {
            case "pdf" -> "/images/PDF.png";
            case "doc", "docx" -> "/images/DOCX.png";
            case "xls", "xlsx" -> "/images/XLSX.png";
            case "png", "jpg", "jpeg", "gif" -> "/images/IMAGE.png";
            case "mp4", "avi", "mov", "mkv" -> "/images/VIDEO.png";
            default -> "/images/FOLDER.png";
        };

        ImageView iconLabel = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        iconLabel.setFitWidth(40);
        iconLabel.setFitHeight(40);

        Label nameLabel = new Label(fileName);
        nameLabel.setStyle("-fx-text-fill: white;");

        Label sizeLabel = new Label(fileSize);
        sizeLabel.setStyle("-fx-text-fill: white;");

        Label typeLabel = new Label(fileType);
        typeLabel.setStyle("-fx-text-fill: white;");

        VBox fileInfo = new VBox(nameLabel, sizeLabel, typeLabel);
        fileInfo.setSpacing(5);

        // Send button (download)
        ImageView downloadButton = new ImageView(new Image(getClass().getResource("/images/DownloadButton.png").toExternalForm()));
        downloadButton.setFitWidth(40);
        downloadButton.setFitHeight(40);
        downloadButton.setOnMouseClicked(e -> {
            Stage ownerStage = (Stage) downloadButton.getScene().getWindow();
            if (ownerStage == null) ownerStage = new Stage();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName(file.getName());

            File destinationFile = fileChooser.showSaveDialog(ownerStage);
            if (destinationFile != null) {
                try {
                    Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File downloaded to: " + destinationFile.getAbsolutePath());
                } catch (IOException ex) {
                    System.err.println("Download failed: " + ex.getMessage());
                }
            } else {
                System.out.println("Download cancelled.");
            }
        });

        // Layout setup using BorderPane
        VBox iconWrapper = new VBox(iconLabel);
        iconWrapper.setPadding(new Insets(8, 5, 0, 0));

        VBox buttonWrapper = new VBox(downloadButton);
        buttonWrapper.setPadding(new Insets(8, 0, 0, 5));

        BorderPane fileBP = new BorderPane();
        fileBP.setLeft(iconWrapper);
        fileBP.setCenter(fileInfo);
        fileBP.setRight(buttonWrapper);
        fileBP.setPadding(new Insets(5));

        // Styling the file message box
        VBox fileBox = new VBox(fileBP);
        fileBox.setSpacing(5);
        fileBox.setPadding(new Insets(10));
        fileBox.setMaxWidth(400);
        fileBox.setStyle(
            "-fx-background-color: #2b3947;" + // darker WhatsApp-like
            "-fx-background-radius: 16 16 16 4;" +
            "-fx-border-radius: 16 16 16 4;" +
            "-fx-border-color: #ced2d5;" +
            "-fx-border-width: 1;"
        );

        HBox wrapper = new HBox(fileBox);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        wrapper.setPadding(new Insets(5, 10, 5, 10));

        messagesPane.getChildren().add(wrapper);
        Platform.runLater(() -> {
            messagesPane.layout();
            chatScrollPane.setVvalue(1.0); // scroll to bottom
        });
    }


    public void startReceiving(String saveDirectory){
        new Thread(() -> {
            try {
                while (true) {
                    String type = server.getDIS().readUTF();

                    switch(type){
                        case "TEXT" -> {
                            String message = server.receiveText();
                            Platform.runLater(() -> {
                                addReceivedMessage(message);
                                try {
                                    dbManager.insertTextMessage(sender.getUserID(), receiver.getUserID(), message);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        case "FILE" -> {
                            File receivedFile = server.receiveFile(saveDirectory);
                            Platform.runLater(() -> {
                                addReceivedFileBox(receivedFile);
                                try {
                                    dbManager.insertFileMessage(sender.getUserID(), receiver.getUserID(), receivedFile);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            
                                messagesPane.layout(); // Ensure layout is refreshed
                                Platform.runLater(() -> chatScrollPane.setVvalue(1.0)); // Scroll after layout
                            });
                        }
                        default -> System.err.println("Unknown message type: " + type);  
                    }
                }

            } catch (IOException e) {
                Platform.runLater(() -> {
                    System.err.println("Error receiving file: " + e.getMessage());
                });
            }
        }).start();
    }

    public void loadChatHistory(Connection conn, int userAId, int userBId) {
        String sql = "SELECT * FROM messages " +
                    "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY timestamp ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userAId);
            stmt.setInt(2, userBId);
            stmt.setInt(3, userBId);
            stmt.setInt(4, userAId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                boolean isText = rs.getBoolean("is_text");

                if (isText) {
                    String content = rs.getString("content");
                    if (senderId == userAId) {
                        Platform.runLater(() -> addMessage(content));
                    } else {
                        Platform.runLater(() -> addReceivedMessage(content));
                    }
                } else {
                    String fileName = rs.getString("file_name");
                    InputStream fileStream = rs.getBinaryStream("file");
                    File tempFile = File.createTempFile("chatfile_", "_" + fileName);
                    tempFile.deleteOnExit(); // Optional: auto-delete on exit

                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    File finalFile = new File("newNamedFile.ext"); // replace with your desired name and extension
                    boolean success = tempFile.renameTo(finalFile);
                    if (!success) {
                        System.err.println("Failed to rename file.");
                    }

                    if (senderId == userAId) {
                        Platform.runLater(() -> addFileBox(finalFile));
                    } else {
                        Platform.runLater(() -> addReceivedFileBox(finalFile));
                    }
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to load chat history", e.getMessage()));
        }
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public Scene getScene(){
        return scene;
    }


}
