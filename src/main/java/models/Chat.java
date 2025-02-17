package models;

import java.sql.Timestamp;

public class Chat {

    private int id;
    private int communauteId;
    private String nom;
    private ChatType type;
    private Timestamp dateCreation;


    public Chat(){}
    public Chat(int id, int communauteId, String nom, ChatType type, Timestamp dateCreation) {
        this.id = id;
        this.communauteId = communauteId;
        this.nom = nom;
        this.type = type;
        this.dateCreation = dateCreation;

    }

    public Chat(int communauteId, String nom, ChatType type) {
        this.communauteId = communauteId;
        this.nom = nom;
        this.type = type;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public Chat(String nom, ChatType type , int id) {

        this.nom = nom;
        this.type = type;
        this.id = id;
    }

    public Chat( int id) {

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommunauteId() {
        return communauteId;
    }

    public void setCommunauteId(int communauteId) {
        this.communauteId = communauteId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", communauteId=" + communauteId +
                ", nom='" + nom + '\'' +
                ", type=" + type +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
