package models;

import java.time.LocalDateTime;

public class Participation {
    private int idParticipation;
    private int idParticipant;
    private int id_hackathon;
    private LocalDateTime dateInscription;
    private boolean estEquipe;
    private String statut;

    public Participation(){

    }
    public Participation( int idHackathon, int idParticipant , LocalDateTime dateInscription, String statut) {
        this.id_hackathon = idHackathon;
        this.idParticipant = idParticipant;
        this.dateInscription = LocalDateTime.now();
        this.statut = statut;
    }
    public Participation(int idParticipation, int idHackathon, int idParticipant , LocalDateTime dateInscription, String statut) {
        this.idParticipation=idParticipation;
        this.id_hackathon = idHackathon;
        this.idParticipant = idParticipant;
        this.dateInscription = LocalDateTime.now();
        this.statut = statut;
    }
    public Participation(int idHackathon) {
        this.id_hackathon=idHackathon;
    }

    public int getIdParticipation() {
        return idParticipation;
    }

    public int getIdHackathon() {
        return id_hackathon;
    }

    public int getIdParticipant() {
        return idParticipant;
    }

    public String getStatut() {
        return statut;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }


    @Override
    public String toString() {
        return "Participation{" +
                "idParticipation=" + idParticipation +
                ", idParticipant=" + idParticipant +
                ", id_hackathon=" + id_hackathon +
                ", statut='" + statut + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
}
