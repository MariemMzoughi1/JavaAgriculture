package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static DataSource instance;
    private Connection connection;

    // Informations de connexion à la base de données
    private final String URL = "jdbc:mysql://localhost:3306/devharvest?useSSL=false&serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    // Constructeur privé pour éviter l'instanciation multiple
    private DataSource() {
        try {
            // Charger le driver JDBC (optionnel à partir de JDBC 4.0)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Connexion réussie à la base de données 'devharvest'");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Le driver JDBC MySQL est introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    // Méthode pour récupérer l'instance unique de DataSource
    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public Connection getConnection() {
        if (connection == null) {
            System.err.println("❌ La connexion est fermée ou non établie.");
            return null;
        }
        return connection;
    }

    // Méthode pour fermer la connexion à la base de données
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connexion fermée avec succès.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
