package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import oracleConnection.ConnectionOracle;

public class StatistiqueDAO {

    /**
     * Récupère tous les déroulements liés au match, dans l'ordre chronologique.
     * Chaque ligne = {id de l'équipe du joueur, mi-temps (1 ou 2), durée de balle en secondes, action}.
     * La durée de balle vient de la vue V_MITEMPS : seconde de l'action - seconde de l'action précédente.
     */
    public static Vector<String[]> getDeroulementsDuMatch(int matchId) {
        Vector<String[]> lignes = new Vector<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return lignes;

            String sql = "SELECT j.equipe_id, v.mitemps, v.duree, v.action_realisee " +
                         "FROM V_MITEMPS v " +
                         "JOIN joueur j ON j.id = v.joueur_id " +
                         "WHERE v.match_id = ? " +
                         "ORDER BY v.seconde, v.id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                lignes.add(new String[] {
                    String.valueOf(rs.getInt("equipe_id")),
                    String.valueOf(rs.getInt("mitemps")),
                    String.valueOf(rs.getInt("duree")),
                    rs.getString("action_realisee")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des déroulements du match : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return lignes;
    }

    /**
     * Récupère les ids des deux équipes du match : {id Home, id Away}.
     * Renvoie {-1, -1} si le match n'existe pas.
     */
    public static int[] getIdsEquipesMatch(int matchId) {
        int[] ids = { -1, -1 };
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return ids;

            String sql = "SELECT equipe_domicile_id, equipe_exterieur_id FROM \"MATCH\" WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                ids[0] = rs.getInt("equipe_domicile_id");
                ids[1] = rs.getInt("equipe_exterieur_id");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des équipes du match : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return ids;
    }
}