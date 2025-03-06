package models;

import java.sql.Date;
import java.sql.Timestamp;

public class Communaute {
    private int id;
    private int id_hackathon;
    private String nom;
    private String description;
    private Timestamp dateCreation;


    public Communaute() {}

    public Communaute(int id, int id_hackathon, String nom, String description, Timestamp dateCreation) {
        this.id = id;
        this.id_hackathon = id_hackathon;  // Use the provided value
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    // Constructor without ID (for new objects)
    public Communaute(int id_hackathon, String nom, String description) {
        this.id_hackathon = id_hackathon;  // Use the provided value
        this.nom = nom;
        this.description = description;
        this.dateCreation = new Timestamp(System.currentTimeMillis()); // Default to current time
    }

    public Communaute( String nom, String description ,int id) {

        this.nom = nom;
        this.description = description;
        this.id = id;
    }

    public Communaute( int id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "Communaute{" +
                "id=" + id +
                ", hackathonId=" + id_hackathon +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHackathonId() {
        return id_hackathon;
    }

    public void setHackathonId(int id_hackathon) {
        this.id_hackathon = id_hackathon;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }
}




