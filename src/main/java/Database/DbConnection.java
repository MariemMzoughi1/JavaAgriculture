package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/devharvest";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DbConnection instance;     // Singleton instance
    private Connection connection;            // Unique JDBC connection

    // Constructeur privé
    private DbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // pour MySQL 8+
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à MySQL établie.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données :");
            e.printStackTrace();
        }
    }

    // Accès à l’instance singleton
    public static DbConnection getInstance() {
        if (instance == null) {
            instance = new DbConnection();
        }
        return instance;
    }

    // Récupérer la connexion
    public static Connection getConn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur lors de la connexion à la base de données : " + e.getMessage());
            return null;
        }
    }


    // Accès direct simplifié (alternative statique)
    public static Connection getConnection() {
        return getInstance().getConn();
    }

    // Fermer proprement la connexion
    public static void closeConnection() {
        try {
            if (getInstance().connection != null && !getInstance().connection.isClosed()) {
                getInstance().connection.close();
                System.out.println("🔌 Déconnexion MySQL effectuée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion :");
            e.printStackTrace();
        }
    }
}
