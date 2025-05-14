package org.example;

import util.MyConnection;

import java.sql.Connection;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
    MyConnection myConn = MyConnection.getInstance();
     Connection conn = myConn.getCnx();

  //      CommunauteService service = new CommunauteService();

        // Create
        //Communaute communaute = new Communaute(0, "Hackathon with chats2", "A community for C++ discussions");
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
//        Communaute commToDelete = new Communaute(5);
//        service.delete(commToDelete);
//        System.out.println("Community deleted successfully!");

//
       // ChatService chatService = new ChatService();
        //    Ajouter un chat
        // Chat chat1 = new Chat( 3);
        //    chatService.add(chat1);

        //chatService.update(chat1);

        // chatService.delete(chat1);


//        MessageService messageService = new MessageService(conn);


        // Create a new message
       // Message newMessage = new Message(2, "Hello, this is a test!", Message.MessageType.QUESTION, new Timestamp(System.currentTimeMillis()), 6);
       // messageService.add(newMessage);


        // Retrieve and display all messages
//        System.out.println("All Messages:");
//        messageService.getAll().forEach(System.out::println);

    }

}