package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    final String URL = "jdbc:mysql://localhost:3306/waw";
    final String USER = "root";
    final String PASS ="";

    Connection cnx;
    static MyConnection instance;



    //privatisation de constructor
    private MyConnection (){
        try{
            cnx = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected ");

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

        public static MyConnection getInstance() {
            if (instance == null) {
                instance = new MyConnection();
            }
            return instance;
        }
        public Connection getCnx() {
            return cnx;
        }


    public Connection getConnection() {
        return cnx;
    }
}
