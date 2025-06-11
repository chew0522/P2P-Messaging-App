package RPC_Project;

public class ZZZChatMessage {
    private String userIconPath;
    private String username;
    private String messageContent;
    private String timestamp;
    private int unreadCount;

    public ZZZChatMessage(String userIconPath, String username, String messageContent, String timestamp, int unreadCount) {
        this.userIconPath = userIconPath;
        this.username = username;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getUserIconPath() {
        return userIconPath;
    }

    public String getUsername() {
        return username;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
