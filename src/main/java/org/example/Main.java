package org.example;

import models.User;
import services.UserService;
import services.WalletServices;
import models.wallet;

import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        UserService ps = new UserService();
        WalletServices ws = new WalletServices();
        try {
           // ps.ajouter(new User(95841109, "farah", "maiti", "farah@farah.com", "123a", PARTICIPANT, "elghazela", "/C:/Users/FARAH/Desktop/Hackify/img/amira.png/"));
           // ps.modifier(new User(55420690, "FARAH", "maiti", "farah@4","1233", role.valueOf("ORGANISATEUR"),"tunis","/C:/Users/FARAH/Desktop/Hackify/img/amira.png/", 7));
           // ps.supprimer(7);
            //System.out.println(ps.recuperer());
            wallet newWallet = new wallet(6, 200.0f, "Initial Deposit");
            ws.ajouter(newWallet);
           // wallet wallet = new wallet(500.0f, "Balance Updated",1);
            //ws.modifier(wallet);
            //ws.supprimer(1);
            //ws.recuperer().forEach(System.out::println);

            System.out.println("DONE !");
        } catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }

}