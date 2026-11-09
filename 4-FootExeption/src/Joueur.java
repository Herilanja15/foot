package modele;

public class Joueur extends Deplacable {
    private int id;
    private String numero;
    private String nom;
    private Equipe equipe;

    // Constructeur
    public Joueur(int id, String numero, String nom, Equipe equipe, double x, double y) {
        super(x, y);
        this.id = id;
        this.numero = numero;
        this.nom = nom;
        this.equipe = equipe;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    // Méthode tirer
    public String tirer(boolean cadre) {
        if (cadre) {
            return "cadre";
        } else {
            return "non cadre";
        }
    }

    @Override
    public String toString() {
        String nomEquipe = (equipe != null) ? equipe.getNom() : "Sans équipe";
        return nomEquipe + ", ID: " + id + ", Num: " + numero + ", Nom: " + nom;
    }
}
