package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private final String URL = "jdbc:mysql://localhost:3306/final";
    private final String USER = "root";
    private final String PASS = "";

    private Connection cnx;
    private static MyConnection instance;

    // Private constructor for Singleton pattern
    private MyConnection() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return cnx;
    }
}
