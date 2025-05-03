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
    public void add(Zone zone) throws SQLException {

        String image = zone.getImage();


        String req = "INSERT INTO Zone (superficie_zone, nom_zone, localisation_zone, image) VALUES ('"
                + zone.getSuperficie_zone() + "', '"
                + zone.getNom_zone() + "', '"
                + zone.getLocalisation_zone() + "', '"
                + (image != null ? image : "") + "')";

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
            pst.setString(3, zone.getNom_zone());
            pst.setInt(4, zone.getId());

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

                System.out.println("❌ Impossible de supprimer cette zone : elle est liée à une ou plusieurs granges.");

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
                        rs.getString("localisation_zone"),
                        rs.getString("image")
                );
                zones.add(z);
            }
        } catch (SQLException e) {
            System.out.println("Erreur dans ZoneService.find() : " + e.getMessage());
        }
        return zones;
    }

    public Zone findById(int id) {
        Zone zone = null;
        String req = "SELECT * FROM Zone WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                zone = new Zone(
                        rs.getInt("id"),
                        rs.getFloat("superficie_zone"),
                        rs.getString("nom_zone"),
                        rs.getString("localisation_zone"),
                        rs.getString("image")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur dans ZoneService.findById() : " + e.getMessage());
        }
        return zone;
    }




}