package models;

import java.util.List;

public class Chapitre {
    private int id;
    private int id_ressources; // Clé étrangère vers Ressource
    private String titre;
    private String url_fichier;
    private String contenu; // Contenu du chapitre (text)
    private String format_fichier; // Format du fichier (ex : PDF, DOCX, etc.)
    private List<Chapitre> chapitres; // Liste des chapitres

    // Constructeur par défaut
    public Chapitre() {}

    // Constructeur avec paramètres
    public Chapitre(int id, int id_ressources, String titre, String url_fichier, String contenu, String format_fichier, List<Chapitre> chapitres) {
        this.id = id;
        this.id_ressources = id_ressources;
        this.titre = titre;
        this.url_fichier = url_fichier;
        this.contenu = contenu;
        this.format_fichier = format_fichier;
        this.chapitres = chapitres;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRessource() {
        return id_ressources;
    }

    public void setIdRessource(int id_ressource) {
        this.id_ressources = id_ressource;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUrlFichier() {
        return url_fichier;
    }

    public void setUrlFichier(String url_fichier) {
        this.url_fichier = url_fichier;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getFormatFichier() {
        return format_fichier;
    }

    public void setFormatFichier(String format_fichier) {
        this.format_fichier = format_fichier;
    }

    public List<Chapitre> getListChapitre() {
        return chapitres;
    }

    public void setListChapitre(List<Chapitre> list_chapitre) {
        this.chapitres = chapitres;
    }

    // Méthode d'affichage
    @Override
    public String toString() {
        return "Chapitre{" +
                "id=" + id +
                ", id_ressources=" + id_ressources +
                ", titre='" + titre + '\'' +
                ", url_fichier='" + url_fichier + '\'' +
                ", contenu='" + contenu + '\'' +
                ", format_fichier='" + format_fichier + '\'' +
                ", list_chapitre=" + chapitres +
                '}';
    }
}

