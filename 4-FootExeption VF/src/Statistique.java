package data;

import dao.StatistiqueDAO;
import java.util.Vector;

public class Statistique {
    private String nom;
    private int matchId;

    // Constructeur
    public Statistique(String nom, int matchId) {
        this.nom = nom;
        this.matchId = matchId;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    // miTemps = 0 : tout le match / 1 : 1ère mi-temps / 2 : 2e mi-temps
    private boolean dansLaPeriode(int miTemps, int miTempsLigne) {
        return miTemps == 0 || miTempsLigne == miTemps;
    }

    // a / (a + b) * 100, ou 0 s'il n'y a rien à compter
    private double pourcentage(double a, double b) {
        if (a + b == 0) {
            return 0.0;
        }
        return (a / (a + b)) * 100.0;
    }

    /**
     * Possession en % : {Home, Away}.
     * Le temps de balle de chaque action (temps de l'action - temps de l'action précédente)
     * est ajouté à l'équipe du joueur qui agit.
     */
    public double[] calculerPourcentagePossession(int miTemps) {
        int[] ids = StatistiqueDAO.getIdsEquipesMatch(matchId);
        Vector<String[]> lignes = StatistiqueDAO.getDeroulementsDuMatch(matchId);

        double tempsHome = 0;
        double tempsAway = 0;

        for (String[] ligne : lignes) {
            int equipeId = Integer.parseInt(ligne[0]);
            int miTempsLigne = Integer.parseInt(ligne[1]);
            int duree = Integer.parseInt(ligne[2]);

            if (dansLaPeriode(miTemps, miTempsLigne)) {
                if (equipeId == ids[0]) {
                    tempsHome += duree;
                } else if (equipeId == ids[1]) {
                    tempsAway += duree;
                }
            }
        }

        return new double[] { pourcentage(tempsHome, tempsAway), pourcentage(tempsAway, tempsHome) };
    }

    /**
     * % de tirs cadrés de chaque équipe : {Home, Away}.
     */
    public double[] calculerPourcentageTirsCadres(int miTemps) {
        int[] cadres = compterTirs(miTemps, "cadre");
        int[] nonCadres = compterTirs(miTemps, "non cadre");

        return new double[] {
            pourcentage(cadres[0], nonCadres[0]),
            pourcentage(cadres[1], nonCadres[1])
        };
    }

    /**
     * % de tirs non cadrés de chaque équipe : {Home, Away}.
     */
    public double[] calculerPourcentageTirsNonCadres(int miTemps) {
        int[] cadres = compterTirs(miTemps, "cadre");
        int[] nonCadres = compterTirs(miTemps, "non cadre");

        return new double[] {
            pourcentage(nonCadres[0], cadres[0]),
            pourcentage(nonCadres[1], cadres[1])
        };
    }

    // Nombre de tirs d'un type ("cadre" ou "non cadre") : {Home, Away}
    private int[] compterTirs(int miTemps, String typeTir) {
        int[] ids = StatistiqueDAO.getIdsEquipesMatch(matchId);
        Vector<String[]> lignes = StatistiqueDAO.getDeroulementsDuMatch(matchId);

        int home = 0;
        int away = 0;

        for (String[] ligne : lignes) {
            int equipeId = Integer.parseInt(ligne[0]);
            int miTempsLigne = Integer.parseInt(ligne[1]);
            String action = ligne[3];

            if (dansLaPeriode(miTemps, miTempsLigne) && typeTir.equalsIgnoreCase(action)) {
                if (equipeId == ids[0]) {
                    home++;
                } else if (equipeId == ids[1]) {
                    away++;
                }
            }
        }

        return new int[] { home, away };
    }

    @Override
    public String toString() {
        return "Statistique [Nom: " + nom + ", Match ID: " + matchId + "]";
    }
}