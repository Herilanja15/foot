package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import modele.Equipe;
import modele.Joueur;
import oracleConnection.ConnectionOracle;

public class EquipeDAO {

    // Récupérer toutes les équipes (sans leurs joueurs) pour les listes déroulantes
    public static Vector<Equipe> getToutesLesEquipes() {
        Vector<Equipe> equipes = new Vector<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return equipes;

            pstmt = conn.prepareStatement("SELECT id, nom FROM equipe ORDER BY nom");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                equipes.add(new Equipe(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des équipes : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return equipes;
    }

    // Récupérer une équipe par son ID (avec ses joueurs associés)
    public static Equipe getEquipeByID(int idEquipe) {
        Equipe equipe = null;
        Connection conn = null;
        PreparedStatement pstmtEquipe = null;
        PreparedStatement pstmtJoueurs = null;
        ResultSet rsEquipe = null;
        ResultSet rsJoueurs = null;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return null;

            // 1. Récupérer l'équipe
            String sqlEquipe = "SELECT id, nom FROM equipe WHERE id = ?";
            pstmtEquipe = conn.prepareStatement(sqlEquipe);
            pstmtEquipe.setInt(1, idEquipe);
            rsEquipe = pstmtEquipe.executeQuery();

            if (rsEquipe.next()) {
                int id = rsEquipe.getInt("id");
                String nom = rsEquipe.getString("nom");
                equipe = new Equipe(id, nom);

                // 2. Récupérer les joueurs de cette équipe
                String sqlJoueurs = "SELECT id, numero, nom, x, y FROM joueur WHERE equipe_id = ? ORDER BY id";
                pstmtJoueurs = conn.prepareStatement(sqlJoueurs);
                pstmtJoueurs.setInt(1, idEquipe);
                rsJoueurs = pstmtJoueurs.executeQuery();

                while (rsJoueurs.next()) {
                    int jId = rsJoueurs.getInt("id");
                    String jNum = rsJoueurs.getString("numero");
                    String jNom = rsJoueurs.getString("nom");
                    double jX = rsJoueurs.getDouble("x");
                    double jY = rsJoueurs.getDouble("y");

                    Joueur joueur = new Joueur(jId, jNum, jNom, equipe, jX, jY);
                    equipe.ajouterJoueur(joueur);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'équipe par ID : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rsJoueurs);
            ConnectionOracle.closeResultSet(rsEquipe);
            if (pstmtJoueurs != null) try { pstmtJoueurs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmtEquipe != null) try { pstmtEquipe.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return equipe;
    }

    // Récupérer une équipe par son nom
    public static Equipe getEquipeByNom(String nomEquipeRecherche) {
        Equipe equipe = null;
        Connection conn = null;
        PreparedStatement pstmtEquipe = null;
        PreparedStatement pstmtJoueurs = null;
        ResultSet rsEquipe = null;
        ResultSet rsJoueurs = null;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return null;

            String sqlEquipe = "SELECT id, nom FROM equipe WHERE nom = ?";
            pstmtEquipe = conn.prepareStatement(sqlEquipe);
            pstmtEquipe.setString(1, nomEquipeRecherche);
            rsEquipe = pstmtEquipe.executeQuery();

            if (rsEquipe.next()) {
                int id = rsEquipe.getInt("id");
                String nom = rsEquipe.getString("nom");
                equipe = new Equipe(id, nom);

                String sqlJoueurs = "SELECT id, numero, nom, x, y FROM joueur WHERE equipe_id = ? ORDER BY id";
                pstmtJoueurs = conn.prepareStatement(sqlJoueurs);
                pstmtJoueurs.setInt(1, id);
                rsJoueurs = pstmtJoueurs.executeQuery();

                while (rsJoueurs.next()) {
                    int jId = rsJoueurs.getInt("id");
                    String jNum = rsJoueurs.getString("numero");
                    String jNom = rsJoueurs.getString("nom");
                    double jX = rsJoueurs.getDouble("x");
                    double jY = rsJoueurs.getDouble("y");

                    Joueur joueur = new Joueur(jId, jNum, jNom, equipe, jX, jY);
                    equipe.ajouterJoueur(joueur);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'équipe par nom : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rsJoueurs);
            ConnectionOracle.closeResultSet(rsEquipe);
            if (pstmtJoueurs != null) try { pstmtJoueurs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmtEquipe != null) try { pstmtEquipe.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return equipe;
    }
}