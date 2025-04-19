package Services;

import Entites.Grange;
import Entites.Zone;
import Interfaces.InterfaceCRUD;
import Utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrangeService implements InterfaceCRUD<Grange> {
    Connection con;

    public GrangeService() {
        con = MyDB.getInstance().getCon();
    }

    public void add(Grange g) {
        // Vérification des données avant insertion
        if (g == null) {
            System.out.println("Erreur : L'objet Grange ne peut pas être nul.");
            return;
        }

        if (g.getType_grange() == null || g.getType_grange().trim().isEmpty()) {
            System.out.println("Erreur : Le type de grange est requis.");
            return;
        }

        if (g.getCapacite() <= 0) {
            System.out.println("Erreur : La capacité doit être supérieure à zéro.");
            return;
        }

        if (g.getZone() == null || g.getZone().getId() <= 0) {
            System.out.println("Erreur : Zone invalide.");
            return;
        }

        String req = "INSERT INTO grange (type_grange, capacite, zone_id) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = MyDB.getInstance().getCon().prepareStatement(req);
            ps.setString(1, g.getType_grange());
            ps.setFloat(2, g.getCapacite());
            ps.setInt(3, g.getZone().getId()); // Liaison avec la zone
            ps.executeUpdate();
            System.out.println("Grange ajoutée avec succès.");
        } catch (SQLException e) {
            // Affichage de l'exception dans la console pour débogage
            System.out.println("Erreur lors de l'ajout de la grange : " + e.getMessage());
            // Optionnel : relancer l'exception ou enregistrer dans un log pour plus de clarté
            e.printStackTrace();
        }
    }


    @Override
    public void update(Grange grange) {
        String req = "UPDATE grange SET type_grange = ?, capacite = ?, zone_id = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, grange.getType_grange());
            ps.setFloat(2, grange.getCapacite());
            ps.setInt(3, grange.getZone().getId());
            ps.setInt(4, grange.getId());

            ps.executeUpdate();
            System.out.println("Grange mise à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la grange : " + e.getMessage());
        }
    }


    @Override
    public void delete(Grange grange) {
        String req = "DELETE FROM grange WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, grange.getId()); // Utilise l'ID de la grange
            ps.executeUpdate();
            System.out.println("Grange supprimée");
        } catch (SQLException e) {
            System.out.println("Erreur de suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Grange> find() {
        List<Grange> granges = new ArrayList<>();
        String req = "SELECT g.*, z.id AS zid, z.superficie_zone, z.nom_zone, z.localisation_zone " +
                "FROM grange g " +
                "JOIN zone z ON g.zone_id = z.id";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Grange g = new Grange();
                g.setId(rs.getInt("id"));
                g.setType_grange(rs.getString("type_grange"));
                g.setCapacite(rs.getFloat("capacite"));

                // Récupérer et construire la zone associée
                Zone z = new Zone();
                z.setId(rs.getInt("zid"));
                z.setSuperficie_zone(rs.getFloat("superficie_zone"));
                z.setNom_zone(rs.getString("nom_zone"));
                z.setLocalisation_zone(rs.getString("localisation_zone"));

                g.setZone(z);

                granges.add(g);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur récupération granges : " + e.getMessage());
        }

        return granges;
    }

}
