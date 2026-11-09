package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modele.Match;
import oracleConnection.ConnectionOracle;

public class MatchDAO {

    // Ajoute un nouveau match dans la base et retourne l'ID généré
    public static int insererMatch(Match match) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = ConnectionOracle.getConnection();
            if (conn == null) return -1;

            int idDomicile = match.getEquipeHome().getId();
            int idExterieur = match.getEquipeAway().getId();
            String dateMatch = match.getDateMatch();

            // Requête d'insertion dans la table "MATCH" avec la séquence match_seq
            String sql = "INSERT INTO \"MATCH\" (id, equipe_domicile_id, equipe_exterieur_id, date_match) VALUES (match_seq.NEXTVAL, ?, ?, ?)";
            
            // Récupération de l'ID généré
            String[] generatedColumns = {"id"};
            pstmt = conn.prepareStatement(sql, generatedColumns);
            pstmt.setInt(1, idDomicile);
            pstmt.setInt(2, idExterieur);
            pstmt.setString(3, dateMatch);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    generatedId = rs.getInt(1);
                    match.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du match : " + e.getMessage());
        } finally {
            ConnectionOracle.closeResultSet(rs);
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            ConnectionOracle.closeConnection(conn);
        }

        return generatedId;
    }
}