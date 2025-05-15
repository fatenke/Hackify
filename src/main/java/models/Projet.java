package models;

public class Projet {
    int id_pr;
    String nom;
    String statut;
    String priorite;
    String description;
    String ressource; // New attribute added

    public Projet() {}

    public Projet(int id_pr, String nom, String statut, String priorite, String description, String ressource) {
        this.id_pr = id_pr;
        this.nom = nom;
        this.statut = statut;
        this.priorite = priorite;
        this.description = description;
        this.ressource = ressource;
    }

    public Projet(String nom, String statut, String priorite, String description, String ressource) {
        this.nom = nom;
        this.statut = statut;
        this.priorite = priorite;
        this.description = description;
        this.ressource = ressource;
    }

    public static void add(Projet p) {
    }

    public int getId_pr() {
        return id_pr;
    }

    public void setId_pr(int id_pr) {
        this.id_pr = id_pr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getRessource() { // New getter for ressource
        return ressource;
    }

    public void setRessource(String ressource) { // New setter for ressource
        this.ressource = ressource;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id_pr=" + id_pr +
                ", nom='" + nom + '\'' +
                ", statut='" + statut + '\'' +
                ", priorite='" + priorite + '\'' +
                ", description='" + description + '\'' +
                ", ressource='" + ressource + '\'' + // Added ressource to toString
                '}';
    }
}