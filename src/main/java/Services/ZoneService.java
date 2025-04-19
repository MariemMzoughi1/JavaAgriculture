package Services;

import Entites.Zone;
import Interfaces.InterfaceCRUD;
import Utils.MyDB;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;



public class ZoneService implements InterfaceCRUD<Zone> {
    Connection con;
    public ZoneService() {
        con= MyDB.getInstance().getCon();
    }

    @Override
    public void add(Zone zone)throws SQLException {
        String req = "INSERT INTO Zone (superficie_zone, nom_zone, localisation_zone) VALUES ('"
                + zone.getSuperficie_zone() + "', '"
                + zone.getNom_zone() + "', '"
                + zone.getLocalisation_zone() + "')";

        Statement st;
        try {
            st = con.createStatement();
            st.executeUpdate(req);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void update(Zone zone) {
        String req = "UPDATE zone SET superficie_zone = ?, localisation_zone = ?, nom_zone = ? WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(req)) {
            pst.setFloat(1, zone.getSuperficie_zone());
            pst.setString(2, zone.getLocalisation_zone());
            pst.setString(3, zone.getNom_zone()); // ← nouvelle valeur du nom
            pst.setInt(4, zone.getId()); // ← condition sur l'id

            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Zone mise à jour avec succès.");
            } else {
                System.out.println("⚠️ Aucune zone mise à jour (id introuvable ?).");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur mise à jour zone : " + e.getMessage());
        }
    }



    @Override
    public void delete(Zone zone) {
        String req = "DELETE FROM zone WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(req)) {
            pst.setInt(1, zone.getId());
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("🗑️ Zone supprimée avec succès.");
            } else {
                System.out.println("⚠️ Aucune zone supprimée (id introuvable ?).");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("a foreign key constraint fails")) {
                // ➕ Message clair dans la console
                System.out.println("❌ Impossible de supprimer cette zone : elle est liée à une ou plusieurs granges.");
                // ➕ Lever une exception personnalisée
                throw new RuntimeException("Impossible de supprimer cette zone car elle est liée à une ou plusieurs granges.");
            } else {
                System.out.println("❌ Erreur suppression zone : " + e.getMessage());
                throw new RuntimeException("Erreur lors de la suppression de la zone.");
            }
        }
    }






    @Override
    public List<Zone> find() {
        List<Zone> zones = new ArrayList<>();
        String req = "SELECT * FROM Zone";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Zone z = new Zone(
                        rs.getInt("id"),
                        rs.getFloat("superficie_zone"),
                        rs.getString("nom_zone"),
                        rs.getString("localisation_zone")
                );
                zones.add(z); // Ajout de la zone à la liste
            }
        } catch (SQLException e) {
            System.out.println("Erreur dans ZoneService.find() : " + e.getMessage());
        }
        return zones;
    }



}