package services;

import Database.DbConnection;
import entities.Parcelle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelleService {

    private Connection connection;

    public ParcelleService() {
        connection = DbConnection.getInstance().getConn();
    }

    public void ajouter(Parcelle parcelle) {
        String sql = "INSERT INTO parcelle (culture_actuelle_id, description, zone, superficie, prix_de_location, date_de_location, date_de_fin_location, etat, type_sol, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parcelle.getCultureActuelleId());
            ps.setString(2, parcelle.getDescription());
            ps.setString(3, parcelle.getZone());
            ps.setDouble(4, parcelle.getSuperficie());
            ps.setDouble(5, parcelle.getPrixDeLocation());
            ps.setDate(6, Date.valueOf(parcelle.getDateDeLocation()));
            ps.setDate(7, Date.valueOf(parcelle.getDateDeFinLocation()));
            ps.setString(8, parcelle.getEtat());
            ps.setString(9, parcelle.getTypeSol());
            ps.setString(10, parcelle.getImage());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Parcelle> afficher() {
        List<Parcelle> list = new ArrayList<>();
        String sql = "SELECT * FROM parcelle";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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

    public void modifier(Parcelle parcelle) {
        String sql = "UPDATE parcelle SET culture_actuelle_id=?, description=?, zone=?, superficie=?, prix_de_location=?, date_de_location=?, date_de_fin_location=?, etat=?, type_sol=?, image=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parcelle.getCultureActuelleId());
            ps.setString(2, parcelle.getDescription());
            ps.setString(3, parcelle.getZone());
            ps.setDouble(4, parcelle.getSuperficie());
            ps.setDouble(5, parcelle.getPrixDeLocation());
            ps.setDate(6, Date.valueOf(parcelle.getDateDeLocation()));
            ps.setDate(7, Date.valueOf(parcelle.getDateDeFinLocation()));
            ps.setString(8, parcelle.getEtat());
            ps.setString(9, parcelle.getTypeSol());
            ps.setString(10, parcelle.getImage());
            ps.setInt(11, parcelle.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM parcelle WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

}
