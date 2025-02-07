package org.example;

import models.Hackathon;
import services.HackathonService;
import util.MyConnection;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        //Connection connection = MyConnection.getInstance().getCnx();

        HackathonService hackathonService = new HackathonService();

        Hackathon hackathon = new Hackathon(
                0,
                "Hack4Change",
                "frre",
                "ddecfv",
                LocalDateTime.of(2025, 6, 10, 9, 0),
                LocalDateTime.of(2025, 6, 12, 18, 0),
                "Tunis",
                "Ouvert aux étudiants et professionnels"
        );

        //hackathonService.add(hackathon);


        Hackathon hackathon1 = new Hackathon(
                1,
                2,
                "Hack4Impact",
                "Un hackathon pour l'impact social",
                "Tech for Good",
                LocalDateTime.of(2025, 7, 15, 9, 0),
                LocalDateTime.of(2025, 7, 17, 18, 0),
                "Sfax",
                "Ouvert aux étudiants et aux startups"
        );


        hackathonService.update(hackathon1);

        List<Hackathon> hackathons = hackathonService.getAll();

        if (hackathons.isEmpty()) {
            System.out.println(" Aucun hackathon trouvé !");
        } else {
            for (Hackathon h : hackathons) {
                System.out.println(hackathons);
            }
        }

        hackathonService.delete(hackathon1);





    }

}