package modele;

import java.util.Vector;

public class Equipe {
    private int id;
    private String nom;
    private Vector<Joueur> joueurs;

    // Constructeur
    public Equipe(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.joueurs = new Vector<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Vector<Joueur> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(Vector<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    // Méthode ajouterJoueur
    public void ajouterJoueur(Joueur j) {
        if (this.joueurs == null) {
            this.joueurs = new Vector<>();
        }
        this.joueurs.add(j);
    }

    @Override
    public String toString() {
        int nbJoueurs = (joueurs != null) ? joueurs.size() : 0;
        return "Nom équipe: " + nom + ", ID: " + id + ", Nombre de joueurs: " + nbJoueurs;
    }
}