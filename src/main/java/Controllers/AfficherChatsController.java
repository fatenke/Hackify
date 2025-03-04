package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Chat;
import models.Message;
import models.Message.MessageType;
import services.ChatService;
import services.GeminiService;
import services.MessageService;
import util.MyConnection;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class AfficherChatsController {

    @FXML
    private Label chatTitle;

    @FXML
    private ListView<String> chatsListView;  // Sidebar: remains as String list for chat names.

    // Change messagesListView to hold Message objects.
    @FXML
    private ListView<Message> messagesListView;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private Button returnButton;

    private ChatService chatService;
    private MessageService messageService;
    private GeminiService geminiService;

    // The community ID passed from the previous view
    private int currentCommunityId;
    private Chat selectedChat;

    // Public setter to receive the community id
    public void setCurrentCommunityId(int id) {
        this.currentCommunityId = id;
        loadChats();
    }

    @FXML
    public void initialize() {
        chatService = new ChatService();
        messageService = new MessageService(MyConnection.getInstance().getCnx());
        geminiService = new GeminiService("AIzaSyAdtU0BkTPvbpbKhK1J6AGNaSwaywhByZc");
        messagesListView.setCellFactory(lv -> new MessageCell());
    }


    private void loadChats() {
        chatsListView.getItems().clear();
        List<Chat> communityChats = chatService.getChatsByCommunityId(currentCommunityId);
        for (Chat c : communityChats) {
            chatsListView.getItems().add(c.getNom());
        }

        // Add a listener so that when a chat is selected, its messages are loaded.
        chatsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Find the chat object corresponding to the selected name.
                List<Chat> communityChatsList = chatService.getChatsByCommunityId(currentCommunityId);
                for (Chat c : communityChatsList) {
                    if (c.getNom().equals(newVal)) {
                        selectedChat = c;
                        chatTitle.setText(c.getNom());
                        loadMessagesForChat(c.getId());
                        break;
                    }
                }
            }
        });

        // Optionally, select the first chat automatically.
        if (!chatsListView.getItems().isEmpty()) {
            chatsListView.getSelectionModel().select(0);
        }
    }

    private void loadMessagesForChat(int chatId) {
        List<Message> chatMessages = messageService.getMessagesByChatId(chatId);
        messagesListView.getItems().setAll(chatMessages);
    }

    @FXML
    private void handleSendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || selectedChat == null) return;

        // Create and save user message
        Message newMessage = new Message(
                selectedChat.getId(),
                content,
                MessageType.QUESTION,
                new Timestamp(System.currentTimeMillis()),
                6 // Current user ID
        );

        messageService.add(newMessage);

        // Clear field and refresh messages
        messageField.clear();
        loadMessagesForChat(selectedChat.getId());
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainLayout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Custom ListCell for displaying Message objects with Edit and Delete options for user id 6.
    private class MessageCell extends ListCell<Message> {
        private HBox hbox = new HBox(10);
        private Label messageLabel = new Label();
        private Button editButton = new Button("Edit");
        private Button deleteButton = new Button("Delete");
        private TextField editTextField = new TextField();
        private Button saveButton = new Button("Save");
        private Button cancelButton = new Button("Cancel");
        private HBox buttonBox = new HBox(5);
        private HBox editBox = new HBox(5);
        private Message currentMessage;


        public MessageCell() {
            hbox.getChildren().addAll(messageLabel, buttonBox);
            buttonBox.setSpacing(5);
            editBox.getChildren().addAll(editTextField, saveButton, cancelButton);

            editButton.setStyle("-fx-background-color: #82e9f1; -fx-text-fill: #1e0425;");
            deleteButton.setStyle("-fx-background-color: #e26ecc; -fx-text-fill: white;");
            saveButton.setStyle("-fx-background-color: #34727e; -fx-text-fill: white;");
            cancelButton.setStyle("-fx-background-color: #ccc; -fx-text-fill: black;");

            // Handle Delete
            deleteButton.setOnAction(e -> {
                if (currentMessage != null) {
                    messageService.delete(currentMessage); // Pass the full Message object
                    getListView().getItems().remove(currentMessage);
                }
            });

            // Handle Edit
            editButton.setOnAction(e -> {
                editTextField.setText(currentMessage.getContenu());
                hbox.getChildren().setAll(editBox);
            });

            saveButton.setOnAction(e -> {
                String newContent = editTextField.getText().trim();
                if (!newContent.isEmpty()) {
                    currentMessage.setContenu(newContent);
                    messageService.update(currentMessage);
                    messageLabel.setText("User " + currentMessage.getPostedBy() + ": " + newContent);
                    hbox.getChildren().setAll(messageLabel, buttonBox);
                }
            });

            cancelButton.setOnAction(e -> {
                hbox.getChildren().setAll(messageLabel, buttonBox);
            });
        }

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                currentMessage = item;
                String sender = (item.getPostedBy() == -1) ? "Bot" : "User " + item.getPostedBy();
                messageLabel.setText(sender + ": " + item.getContenu());

                buttonBox.getChildren().clear();
                if (item.getType() == MessageType.QUESTION && item.getPostedBy() == 6) {
                    buttonBox.getChildren().addAll(editButton, deleteButton);
                }

                setGraphic(hbox);
            }
        }

    }
}
