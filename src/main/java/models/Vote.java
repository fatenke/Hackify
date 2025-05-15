package models;

public class Vote {
    int id,idEvaluation, idVotant, idProjet, idHackathon;
    float valeurVote;
    String date;
    public Vote() {}
    public Vote(int id,int idEvaluation, int idVotant, int idProjet, int idHackathon, float valeurVote, String date ) {
        this.id = id;
        this.idEvaluation = idEvaluation;
        this.idVotant = idVotant;
        this.idProjet = idProjet;
        this.idHackathon = idHackathon;
        this.valeurVote = valeurVote;
        this.date = date;
    }
    public Vote(float valeurVote, String date , int idEvaluation, int idVotant, int idProjet, int idHackathon) {
        this.valeurVote = valeurVote;
        this.date = date;
        this.idEvaluation = idEvaluation;
        this.idVotant = idVotant;
        this.idProjet = idProjet;
        this.idHackathon = idHackathon;

    }
    public int getId() {
        return id;
    }

    public int getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(int idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getIdVotant() {
        return idVotant;
    }
    public void setIdVotant(int idVotant) {
        this.idVotant = idVotant;
    }
    public int getIdProjet() {
        return idProjet;
    }
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }
    public int getIdHackathon() {
        return idHackathon;
    }
    public void setIdHackathon(int idHackathon) {
        this.idHackathon = idHackathon;
    }
    public float getValeurVote() {
        return valeurVote;
    }
    public void setValeurVote(float valeurVote) {
        this.valeurVote = valeurVote;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", idEvaluation=" + idEvaluation +
                ", idVotant=" + idVotant +
                ", idProjet=" + idProjet +
                ", idHackathon=" + idHackathon +
                ", valeurVote=" + valeurVote +
                ", date='" + date + '\'' +
                '}';
    }
}
