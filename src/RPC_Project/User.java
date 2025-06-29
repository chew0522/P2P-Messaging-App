package RPC_Project;

import java.sql.SQLException;

public class User{
    private Main app;
    private int user_id;
    private String username;
    private DatabaseManager dbManager;
    private String email;
    private String password;
    

    public User(Main app, String email, String password){
        this.app = app;
        this.email = email;
        this.password = password;
        this.dbManager = app.getDatabaseManager();
        setUserID(email, password);
        setUsername(email, password);
    }

    public int getUserID(){
        return this.user_id;
    }

    public void setUserID(String email, String password){
        try {
            this.user_id = dbManager.getUserID(email, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String email, String password){
        try {
            this.username = dbManager.getUsername(email, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getEmail(){
        return this.email;
    }
}