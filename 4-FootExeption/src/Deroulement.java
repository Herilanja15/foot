package data;

import java.util.Vector;
import modele.Joueur;

public class Deroulement {
    private int idMatch;
    private Vector<Integer> joueursIds;
    private Vector<String> numerosJoueurs;
    private Vector<String> actions;
    private Vector<Integer> tempsDuMatch;

    // Constructeur
    public Deroulement(int idMatch) {
        this.idMatch = idMatch;
        this.joueursIds = new Vector<>();
        this.numerosJoueurs = new Vector<>();
        this.actions = new Vector<>();
        this.tempsDuMatch = new Vector<>();
    }

    // Getters et Setters
    public int getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }

    public Vector<Integer> getJoueursIds() {
        return joueursIds;
    }

    public void setJoueursIds(Vector<Integer> joueursIds) {
        this.joueursIds = joueursIds;
    }

    public Vector<String> getNumerosJoueurs() {
        return numerosJoueurs;
    }

    public void setNumerosJoueurs(Vector<String> numerosJoueurs) {
        this.numerosJoueurs = numerosJoueurs;
    }

    public Vector<String> getActions() {
        return actions;
    }

    public void setActions(Vector<String> actions) {
        this.actions = actions;
    }

    public Vector<Integer> getTempsDuMatch() {
        return tempsDuMatch;
    }

    public void setTempsDuMatch(Vector<Integer> tempsDuMatch) {
        this.tempsDuMatch = tempsDuMatch;
    }

    // Ajoute une étape : le joueur, son action et la seconde du match
    public void ajouteEtape(Joueur j, String action, int temps) {
        if (j != null) {
            this.joueursIds.add(j.getId());
            this.numerosJoueurs.add(j.getNumero());
            this.actions.add(action);
            this.tempsDuMatch.add(temps);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int size = actions.size();
        for (int i = 0; i < size; i++) {
            sb.append("(idMatch: ").append(idMatch)
              .append(" , TempsDeJeu: ").append(tempsDuMatch.get(i)).append("s")
              .append(" , numJoueur: ").append(numerosJoueurs.get(i))
              .append(" , idJoueur: ").append(joueursIds.get(i))
              .append(" , Action: ").append(actions.get(i)).append(")\n");
        }
        return sb.toString();
    }
}