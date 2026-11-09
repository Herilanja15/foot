package modele;

public class Match {
    private int id;
    private Equipe equipeHome;
    private Equipe equipeAway;
    private String dateMatch;
    private int scoreHome;
    private int scoreAway;

    // Constructeur
    public Match(int id, Equipe equipeHome, Equipe equipeAway, String dateMatch, int scoreHome, int scoreAway) {
        this.id = id;
        this.equipeHome = equipeHome;
        this.equipeAway = equipeAway;
        this.dateMatch = dateMatch;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipe getEquipeHome() {
        return equipeHome;
    }

    public void setEquipeHome(Equipe equipeHome) {
        this.equipeHome = equipeHome;
    }

    public Equipe getEquipeAway() {
        return equipeAway;
    }

    public void setEquipeAway(Equipe equipeAway) {
        this.equipeAway = equipeAway;
    }

    public String getDateMatch() {
        return dateMatch;
    }

    public void setDateMatch(String dateMatch) {
        this.dateMatch = dateMatch;
    }

    public int getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(int scoreHome) {
        this.scoreHome = scoreHome;
    }

    public int getScoreAway() {
        return scoreAway;
    }

    public void setScoreAway(int scoreAway) {
        this.scoreAway = scoreAway;
    }

    // Ajoute un but (+1) pour l'équipe donnée
    public void ajoutePourEquipe(Equipe e) {
        if (e != null) {
            if (this.equipeHome != null && e.getId() == this.equipeHome.getId()) {
                this.scoreHome++;
            } else if (this.equipeAway != null && e.getId() == this.equipeAway.getId()) {
                this.scoreAway++;
            }
        }
    }

    @Override
    public String toString() {
        String nomHome = (equipeHome != null) ? equipeHome.getNom() : "Inconnu";
        String nomAway = (equipeAway != null) ? equipeAway.getNom() : "Inconnu";
        return "Match ID: " + id + " | " + nomHome + " vs " + nomAway + " | Date: " + dateMatch;
    }
}