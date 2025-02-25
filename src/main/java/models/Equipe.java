package models;

public class Equipe {
    private int id_equipe;
    private String nom_equipe;
    private int id_chef_equipe;
    private int id_hackathon;

    public Equipe() {
    }
    public Equipe(String nom_equipe) {
        this.nom_equipe=nom_equipe;
    }

    public int getId_chef_equipe() {
        return id_chef_equipe;
    }

    public int getId_equipe() {
        return id_equipe;
    }

    public int getId_hackathon() {
        return id_hackathon;
    }

    public String getNom_equipe() {
        return nom_equipe;
    }

    public void setId_chef_equipe(int id_chef_equipe) {
        this.id_chef_equipe = id_chef_equipe;
    }

    public void setId_equipe(int id_equipe) {
        this.id_equipe = id_equipe;
    }

    public void setId_hackathon(int id_hackathon) {
        this.id_hackathon = id_hackathon;
    }

    public void setNom_equipe(String nom_equipe) {
        this.nom_equipe = nom_equipe;
    }


}
