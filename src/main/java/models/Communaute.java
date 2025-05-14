package models;

import java.sql.Timestamp;

public class Communaute {
    private int id;
    private int idHackathon; // Foreign key to hackathon
    private String nom; // Community name
    private String description; // Community description
    private Timestamp dateCreation; // Creation timestamp
    private boolean isActive; // Whether the community is active

    // Default constructor
    public Communaute() {
    }

    // Constructor for creating a new community
    public Communaute(int idHackathon, String nom, String description) {
        this.idHackathon = idHackathon;
        this.nom = nom;
        this.description = description;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
    }

    // Constructor for retrieving from database
    public Communaute(int id, int idHackathon, String nom, String description, Timestamp dateCreation, boolean isActive) {
        this.id = id;
        this.idHackathon = idHackathon;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdHackathon() {
        return idHackathon;
    }

    public void setIdHackathon(int idHackathon) {
        this.idHackathon = idHackathon;
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

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Communaute{" +
                "id=" + id +
                ", idHackathon=" + idHackathon +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                ", isActive=" + isActive +
                '}';
    }
}