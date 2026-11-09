package oracleConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionOracle {
    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "1521");
    private static final String SERVICE = System.getenv().getOrDefault("DB_SERVICE", "FREEPDB1");
    private static final String URL = "jdbc:oracle:thin:@//" + HOST + ":" + PORT + "/" + SERVICE;
    private static final String USER = System.getenv().getOrDefault("DB_USER", "footform");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "footform");

    // 1. Fonction de connexion
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC introuvable (Vérifie que ojdbc17.jar est bien dans le classpath/lib) : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données Oracle : " + e.getMessage());
        }
        return conn;
    }

    // 2. Fonction de déconnexion centralisée
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Déconnexion Oracle réussie.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    // 3. Fonction de fermeture pour le Statement
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Fonction de fermeture pour le ResultSet
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}