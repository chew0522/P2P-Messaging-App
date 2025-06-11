package RPC_Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private Connection conn; 
    private String url;
    private String user;
    private String password;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.conn = null;
    }

    public void connect() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            try {
                // Ensure the JDBC driver is loaded (optional for modern JDBC, but good practice)
                // Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL, uncomment if needed
                this.conn = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected successfully!");
            } catch (SQLException e) {
                System.err.println("Failed to connect to database: " + e.getMessage());
                throw e; // Re-throw the exception so the caller can handle it
            }
        } else {
            System.out.println("Database already connected.");
        }
    }

    // Method to disconnect from the database
    public void disconnect() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            try {
                this.conn.close();
                System.out.println("Database disconnected successfully!");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
                throw e; // Re-throw the exception
            }
        }
    }

    // 1. Insert message
    public boolean insertMessage(int senderId, int receiverId, String content, boolean isFile, String filePath) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, is_file, file_path) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.setString(4, isFile ? "Y" : "N");
            stmt.setString(5, filePath);
            return stmt.executeUpdate() > 0;
        }
    }

    // 2. Delete message by ID (optional use case)
    public boolean deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        }
    }

    // 3. Retrieve chat history between two users
    public List<String> getChatHistory(int userA, int userB) throws SQLException {
        String sql = "SELECT sender_id, receiver_id, content, is_file, file_path, sent_at " +
                     "FROM messages " +
                     "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                     "ORDER BY sent_at ASC";

        List<String> history = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userA);
            stmt.setInt(2, userB);
            stmt.setInt(3, userB);
            stmt.setInt(4, userA);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int sender = rs.getInt("sender_id");
                String content = rs.getString("content");
                boolean isFile = "Y".equals(rs.getString("is_file"));
                String filePath = rs.getString("file_path");
                Timestamp timestamp = rs.getTimestamp("sent_at");

                if (isFile) {
                    history.add("[" + timestamp + "] User " + sender + " sent a file: " + filePath);
                } else {
                    history.add("[" + timestamp + "] User " + sender + ": " + content);
                }
            }
        }
        return history;
    }

    // 4. Register new user
    public boolean registerUser(String email, String password, String username) throws SQLException {
        String sql = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, username);
            return stmt.executeUpdate() > 0;
        }
    }

    // 5. Login validation
    public int getSenderID(String email, String password) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id"); // return valid user_id
            }
        }
        return -1; // invalid login
    }

    public boolean validateLogin(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // return valid user_id
            }
        }
        return false; // invalid login
    }

    public String getUsername(String email, String password) throws SQLException{
        String sql = "SELECT username FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            } else {
                return null;
            }
        }
    }

    public boolean checkUsernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count is > 0, username exists
            }
        }
        return false; // Should not be reached if query is successful
    }

    public boolean checkEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count is > 0, email exists
            }
        }
        return false; // Should not be reached if query is successful
    }
}
