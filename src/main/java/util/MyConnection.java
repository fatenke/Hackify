package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    final String URL = "jdbc:mysql://localhost:3306/fina";
    final String USER = "root";
    final String PASS ="";

    Connection cnx;
    static MyConnection instance;



    //privatisation de constructeur
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




}
