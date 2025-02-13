package org.example;

import models.Chat;
import models.ChatType;
import models.Communaute;
import services.ChatService;
import services.CommunauteService;
import util.MyConnection;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        MyConnection myConn = MyConnection.getInstance();
//        Connection conn = myConn.getCnx();

         CommunauteService service = new CommunauteService();

        // Create
        //Communaute communaute = new Communaute(0, "Hackathon C++", "A community for C++ discussions");
        //service.add(communaute);

        //update
        // Communaute updatedComm = new Communaute( "Updated Name", "Updated Description" ,1 );
        //service.update(updatedComm);
        //System.out.println("Community updated successfully!");

        //getall
//        List<Communaute> communities = service.getAll();
//        for (Communaute c : communities) {
//            System.out.println(c);
//        }


        //delete
//        Communaute commToDelete = new Communaute(1);
//
//        service.delete(commToDelete);
//
//        System.out.println("Community deleted successfully!");

//
        ChatService chatService = new ChatService();
        //    Ajouter un chat
         Chat chat1 = new Chat( 3);
        //    chatService.add(chat1);

        //chatService.update(chat1);

       // chatService.delete(chat1);



    }

}