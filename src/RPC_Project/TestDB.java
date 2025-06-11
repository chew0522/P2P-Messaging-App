package RPC_Project;
import java.sql.*;

import java.net.URL;

public class TestDB {
    public static void main(String[] args) {
        /*
        String url = "jdbc:mysql://localhost:3306/CNS4400"; // Replace testdb with your DB name
        String user = "root";
        String password = "223763";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION()");

            while (rs.next()) {
                System.out.println("MySQL Version: " + rs.getString(1));
            }

            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
            */
        URL imgUrl = TestDB.class.getResource("/images/LoginPage.jpg");
        System.out.println("Image URL: " + imgUrl);

    }
}

