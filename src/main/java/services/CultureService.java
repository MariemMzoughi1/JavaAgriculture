package services;

import Database.DbConnection;
import entities.Culture;
import entities.Parcelle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CultureService {

    private Connection connection;

    public CultureService() {
        connection = DbConnection.getInstance().getConn();
    }

    // 🔼 Ajouter une culture
    public void ajouter(Culture culture) {
        String req = "INSERT INTO culture (nom, description, image, date_plantation, date_recolte, saison, quantite, categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, culture.getNom());
            pst.setString(2, culture.getDescription());
            pst.setString(3, culture.getImage());
            pst.setDate(4, Date.valueOf(culture.getDatePlantation()));
            pst.setDate(5, Date.valueOf(culture.getDateRecolte()));
            pst.setString(6, culture.getSaison());
            pst.setDouble(7, culture.getQuantite());
            pst.setString(8, culture.getCategorie());

            pst.executeUpdate();
            System.out.println("✅ Culture ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    // 📋 Afficher toutes les cultures
    public List<Culture> afficher() {
        List<Culture> cultures = new ArrayList<>();
        String req = "SELECT * FROM culture";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Culture c = new Culture();
                c.setId(rs.getInt("id"));
                c.setNom(rs.getString("nom"));
                c.setDescription(rs.getString("description"));
                c.setImage(rs.getString("image"));
                c.setDatePlantation(rs.getDate("date_plantation").toLocalDate());
                c.setDateRecolte(rs.getDate("date_recolte").toLocalDate());
                c.setSaison(rs.getString("saison"));
                c.setQuantite(rs.getDouble("quantite"));
                c.setCategorie(rs.getString("categorie"));

                cultures.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage : " + e.getMessage());
        }

        return cultures;
    }

    public void modifier(Culture culture) {
        String req = "UPDATE culture SET nom=?, description=?, image=?, date_plantation=?, date_recolte=?, saison=?, quantite=?, categorie=? WHERE id=?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, culture.getNom());
            pst.setString(2, culture.getDescription());
            pst.setString(3, culture.getImage());
            pst.setDate(4, Date.valueOf(culture.getDatePlantation()));
            pst.setDate(5, Date.valueOf(culture.getDateRecolte()));
            pst.setString(6, culture.getSaison());
            pst.setDouble(7, culture.getQuantite());
            pst.setString(8, culture.getCategorie());
            pst.setInt(9, culture.getId());

            int rowsUpdated = pst.executeUpdate();
            System.out.println("✅ Mise à jour : " + rowsUpdated + " ligne(s) modifiée(s).");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }
    public List<Parcelle> rechercherParEtat(String etat) {
        List<Parcelle> list = new ArrayList<>();
        String sql = "SELECT * FROM parcelle WHERE etat LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + etat + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Parcelle p = new Parcelle(
                        rs.getInt("id"),
                        rs.getInt("culture_actuelle_id"),
                        rs.getString("description"),
                        rs.getString("zone"),
                        rs.getDouble("superficie"),
                        rs.getDouble("prix_de_location"),
                        rs.getDate("date_de_location").toLocalDate(),
                        rs.getDate("date_de_fin_location").toLocalDate(),
                        rs.getString("etat"),
                        rs.getString("type_sol"),
                        rs.getString("image")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean supprimer(int id) {
        try {
            // 1️⃣ Supprimer d'abord les parcelles liées
            String deleteParcelles = "DELETE FROM parcelle WHERE culture_actuelle_id=?";
            try (PreparedStatement pstParcelle = connection.prepareStatement(deleteParcelles)) {
                pstParcelle.setInt(1, id);
                pstParcelle.executeUpdate();
            }

            // 2️⃣ Ensuite, supprimer la culture
            String deleteCulture = "DELETE FROM culture WHERE id=?";
            try (PreparedStatement pstCulture = connection.prepareStatement(deleteCulture)) {
                pstCulture.setInt(1, id);
                int rows = pstCulture.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}
