package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import modele.Joueur;
import oracleConnection.ConnectionOracle;

public class DeroulementDAO {

    /**
     * Insère une nouvelle ligne de déroulement dans la base Oracle.
     */
    public static boolean insererDeroulement(int matchId, int seconde, Joueur joueur, String action, double dx, double dy, Integer cibleId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return false;

            String sql = "INSERT INTO deroulement (id, match_id, seconde, joueur_id, action_realisee, dx, dy, cible_id, mitemps) " +
                         "VALUES (deroulement_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            pstmt.setInt(2, seconde);
            pstmt.setInt(3, joueur.getId());
            pstmt.setString(4, action);
            pstmt.setDouble(5, dx);
            pstmt.setDouble(6, dy);

            if (cibleId != null) {
                pstmt.setInt(7, cibleId);
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            int partie = -1;
            if (seconde <= 2700) {
                partie = 1;
            } else if (seconde > 2700 && seconde <= 5400) {
                partie = 2;
            } else {
                partie = 3;
            }
            pstmt.setDouble(8, partie);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du déroulement : " + e.getMessage());
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return success;
    }

    /**
     * Dernière seconde enregistrée pour le match (0 s'il n'y a encore aucune action).
     * Sert au +1s automatique quand la seconde saisie est vide.
     */
    public static int tempsJoueursPrecedent(int matchId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int derniereSeconde = 0;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return 0;

            String sql = "SELECT MAX(seconde) AS derniere_sec FROM deroulement WHERE match_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                derniereSeconde = rs.getInt("derniere_sec");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du temps précédent : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return derniereSeconde;
    }

    /**
     * Seconde de l'action juste avant la seconde donnée (0 s'il n'y en a pas).
     * Durée de balle du joueur qui agit = seconde - tempsActionPrecedente(matchId, seconde).
     */
    public static int tempsActionPrecedente(int matchId, int seconde) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int secondePrecedente = 0;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return 0;

            String sql = "SELECT MAX(seconde) AS sec_prec FROM deroulement WHERE match_id = ? AND seconde < ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            pstmt.setInt(2, seconde);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                secondePrecedente = rs.getInt("sec_prec");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'action précédente : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return secondePrecedente;
    }

    /**
     * Lignes du déroulement d'un match, dans l'ordre chronologique.
     * Chaque ligne = {seconde, nom du joueur, numéro, nom de l'équipe, action}.
     */
    public static Vector<String[]> listerParMatch(int matchId) {
        Vector<String[]> lignes = new Vector<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return lignes;

            String sql = "SELECT d.seconde, j.nom AS nom_joueur, j.numero, e.nom AS nom_equipe, d.action_realisee " +
                         "FROM deroulement d " +
                         "JOIN joueur j ON j.id = d.joueur_id " +
                         "JOIN equipe e ON e.id = j.equipe_id " +
                         "WHERE d.match_id = ? " +
                         "ORDER BY d.seconde, d.id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                lignes.add(new String[] {
                    String.valueOf(rs.getInt("seconde")),
                    rs.getString("nom_joueur"),
                    rs.getString("numero"),
                    rs.getString("nom_equipe"),
                    rs.getString("action_realisee")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture du déroulement : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return lignes;
    }
}