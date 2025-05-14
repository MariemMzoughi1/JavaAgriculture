package services;

import entities.Commande;
import entities.Produit;
import Database.DbConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeService {

    private Connection con;

    public CommandeService() {
        con = DbConnection.getInstance().getConn();
    }

    public List<Produit> getProduits() {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM produit";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                Produit p = new Produit();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setDescription(rs.getString("description"));
                p.setCategorie(rs.getString("categorie"));
                p.setPrix_unitaire(rs.getInt("prix_unitaire"));
                p.setQuantite_stock(rs.getInt("quantite_stock"));
                p.setImage(rs.getString("image"));
                p.setAgriculteur_id(rs.getInt("agriculteur_id"));
                p.setDate_ajout(rs.getDate("date_ajout").toLocalDate()); // ✅ Correction ici
                produits.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }
        return produits;
    }

    public void ajouterCommande(Commande commande) {
        String req = "INSERT INTO commande (etat, datecommande, total) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, commande.getEtat());
            ps.setTimestamp(2, Timestamp.valueOf(commande.getDatecommande())); // Utilise 'datecommande' au lieu de 'date_commande'
            ps.setDouble(3, commande.getTotal());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int commandeId = generatedKeys.getInt(1);
                    System.out.println("Commande ajoutée avec succès, ID : " + commandeId);
                    // ici tu peux appeler ajouterProduitsCommande(commandeId, listeProduits) si besoin
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }

    private void ajouterProduitsCommande(int commandeId, List<Produit> produits) {
        String req = "INSERT INTO commande_produit (commande_id, produit_id) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(req)) {
            for (Produit produit : produits) {
                ps.setInt(1, commandeId);
                ps.setInt(2, produit.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Produits associés à la commande ajoutés avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout des produits à la commande : " + e.getMessage());
        }
    }

    public List<Produit> getProduitsByCommande(Commande commande) {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT p.* FROM produit p " +
                "JOIN commande_produit cp ON p.id = cp.produit_id " +
                "WHERE cp.commande_id = ?";

        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, commande.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit();
                    produit.setId(rs.getInt("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setDescription(rs.getString("description"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setPrix_unitaire(rs.getInt("prix_unitaire"));
                    produit.setQuantite_stock(rs.getInt("quantite_stock"));
                    produit.setImage(rs.getString("image"));
                    produit.setAgriculteur_id(rs.getInt("agriculteur_id"));
                    produit.setDate_ajout(rs.getDate("date_ajout").toLocalDate());
                    produits.add(produit);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits associés à la commande : " + e.getMessage());
        }
        return produits;
    }

    public void delete(Commande commande) {
    }


    public List<Commande> find() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Commande cmd = new Commande();
                cmd.setEtat(rs.getString("etat"));
                cmd.setDateCommande(rs.getTimestamp("datecommande").toLocalDateTime()); // ou getDate().toLocalDate()
                cmd.setTotal(rs.getDouble("total"));

                commandes.add(cmd);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return commandes;
    }

}
