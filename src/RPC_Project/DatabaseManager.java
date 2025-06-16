package RPC_Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

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

    // Method to start connection to database 
    public void connect() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            try {
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

    // Store text message content into database 
    public void insertTextMessage(int senderId, int receiverId, String message) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, is_text, content) VALUES (?, ?, 1, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, message);
            stmt.executeUpdate();
            System.out.println("Text message inserted.");
        }
    }

    // Store file message content into database 
    public void insertFileMessage(int senderId, int receiverId, File file) throws SQLException, IOException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, is_text, file, file_name) VALUES (?, ?, 0, ?, ?)";

        try (FileInputStream fis = new FileInputStream(file);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setBinaryStream(3, fis, (int) file.length());
            stmt.setString(4, file.getName());
            stmt.executeUpdate();
            System.out.println("File message inserted.");
        }
    }

    // Register new user into database 
    public boolean registerUser(String email, String password, String username) throws SQLException {
        String sql = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, username);
            return stmt.executeUpdate() > 0;
        }
    }

    // Login validation
    public int getUserID(String email, String password) throws SQLException {
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

    // For Sign Up Page to avoid duplicate username registered 
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

    // For Sign Up Page to avoid duplicate email registered
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

    public Connection getConnection(){
        return conn;
    }
}
