package Services;

import Utils.MyDB;
import Entites.Machine;
import Entites.Reservation;
import Entites.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation {
    private final Connection con;
    private final ServiceMachine serviceMachine = new ServiceMachine();
    private final UserService serviceUser = new UserService();

    public ServiceReservation() {
        this.con = MyDB.getInstance().getCon();
    }

    public void addReservation(Reservation r) {
        String sql = "INSERT INTO reservation (date_debut, date_fin, machine_id, client_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, r.getDateDebut());
            stmt.setTimestamp(2, r.getDateFin());
            stmt.setInt(3, r.getMachine().getId());
            stmt.setInt(4, r.getClient().getId());
            stmt.executeUpdate();
            System.out.println("✅ Reservation added.");
        } catch (SQLException e) {
            System.err.println("❌ Error adding reservation:");
            e.printStackTrace();
        }
    }

    public void deleteReservation(Reservation r) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, r.getId());
            stmt.executeUpdate();
            System.out.println("✅ Reservation deleted.");
        } catch (SQLException e) {
            System.err.println("❌ Error deleting reservation:");
            e.printStackTrace();
        }
    }

    public void editReservation(Reservation r) {
        String sql = "UPDATE reservation SET date_debut = ?, date_fin = ?, machine_id = ?, client_id = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, r.getDateDebut());
            stmt.setTimestamp(2, r.getDateFin());
            stmt.setInt(3, r.getMachine().getId());
            stmt.setInt(4, r.getClient().getId());
            stmt.setInt(5, r.getId());
            stmt.executeUpdate();
            System.out.println("✅ Reservation updated.");
        } catch (SQLException e) {
            System.err.println("❌ Error updating reservation:");
            e.printStackTrace();
        }
    }

    public Reservation getReservation(int id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Timestamp dateDebut = rs.getTimestamp("date_debut");
                Timestamp dateFin = rs.getTimestamp("date_fin");
                int machineId = rs.getInt("machine_id");
                int clientId = rs.getInt("client_id");

                Machine machine = serviceMachine.getMachine(machineId);
                User client = serviceUser.getUserById(clientId);

                return new Reservation(id, dateDebut, dateFin, machine, client);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching reservation:");
            e.printStackTrace();
        }
        return null;
    }

    public List<Reservation> getReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp dateDebut = rs.getTimestamp("date_debut");
                Timestamp dateFin = rs.getTimestamp("date_fin");
                int machineId = rs.getInt("machine_id");
                int clientId = rs.getInt("client_id");

                Machine machine = serviceMachine.getMachine(machineId);
                User client = serviceUser.getUserById(clientId);

                if (machine == null || client == null) {
                    System.err.println("⚠️ Skipping reservation ID " + id +
                            " (invalid machine_id: " + machineId + ", client_id: " + clientId + ")");
                    continue;
                }

                Reservation r = new Reservation(id, dateDebut, dateFin, machine, client);
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading reservations:");
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> getReservationsByMachine(int machineId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE machine_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp dateDebut = rs.getTimestamp("date_debut");
                Timestamp dateFin = rs.getTimestamp("date_fin");
                int clientId = rs.getInt("client_id");

                Machine machine = serviceMachine.getMachine(machineId);
                User client = serviceUser.getUserById(clientId);

                if (machine == null || client == null) continue;

                Reservation r = new Reservation(id, dateDebut, dateFin, machine, client);
                list.add(r);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching reservations by machine:");
            e.printStackTrace();
        }

        return list;
    }

    public boolean isReservationAvailable(int machineId, Timestamp start, Timestamp end) {
        String sql = "SELECT COUNT(*) FROM reservation " +
                "WHERE machine_id = ? AND " +
                "((date_debut <= ? AND date_fin >= ?) OR " +
                " (date_debut <= ? AND date_fin >= ?) OR " +
                " (date_debut >= ? AND date_fin <= ?))";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            stmt.setTimestamp(2, start);
            stmt.setTimestamp(3, start);
            stmt.setTimestamp(4, end);
            stmt.setTimestamp(5, end);
            stmt.setTimestamp(6, start);
            stmt.setTimestamp(7, end);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
