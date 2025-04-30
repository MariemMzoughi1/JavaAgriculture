package Services;


import Entites.Machine;
import Utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMachine {
    private final Connection con;

    public ServiceMachine() {
        this.con = MyDB.getInstance().getCon();
    }

    public void addMachine(Machine machine) {
        String sql = "INSERT INTO machine (nom_machine, type, etat, date_dernier_entretien, prix_location_jour, marque, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, machine.getName());
            stmt.setString(2, machine.getType());
            stmt.setString(3, machine.getEtat());
            stmt.setTimestamp(4, machine.getDateLastCheckup());
            stmt.setInt(5, machine.getPricePerDay());
            stmt.setString(6, machine.getBrand());

            if (machine.getImage_url() != null) {
                stmt.setString(7, machine.getImage_url());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }

            stmt.executeUpdate();
            System.out.println("✅ Machine added successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Error adding machine:");
            e.printStackTrace();
        }
    }

    public void editMachine(Machine machine) {
        String sql = "UPDATE machine SET nom_machine = ?, type = ?, etat = ?, date_dernier_entretien = ?, prix_location_jour = ?, marque = ?, image = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, machine.getName());
            stmt.setString(2, machine.getType());
            stmt.setString(3, machine.getEtat());
            stmt.setTimestamp(4, machine.getDateLastCheckup());
            stmt.setInt(5, machine.getPricePerDay());
            stmt.setString(6, machine.getBrand());

            if (machine.getImage_url() != null) {
                stmt.setString(7, machine.getImage_url());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }

            stmt.setInt(8, machine.getId());

            stmt.executeUpdate();
            System.out.println("✅ Machine updated successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Error updating machine:");
            e.printStackTrace();
        }
    }

    public void deleteMachine(Machine machine) {
        String checkSql = "SELECT COUNT(*) FROM reservation WHERE machine_id = ?";
        String deleteSql = "DELETE FROM machine WHERE id = ?";

        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setInt(1, machine.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("❌ Cannot delete machine: it has linked reservations.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking reservations:");
            e.printStackTrace();
            return;
        }

        try (PreparedStatement deleteStmt = con.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, machine.getId());
            deleteStmt.executeUpdate();
            System.out.println("✅ Machine deleted successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Error deleting machine:");
            e.printStackTrace();
        }
    }

    public Machine getMachine(int id) {
        String sql = "SELECT * FROM machine WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Machine(
                        rs.getInt("id"),
                        rs.getString("nom_machine"),
                        rs.getString("type"),
                        rs.getString("etat"),
                        rs.getTimestamp("date_dernier_entretien"),
                        rs.getInt("prix_location_jour"),
                        rs.getString("marque"),
                        rs.getString("image") // may return null — OK
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching machine:");
            e.printStackTrace();
        }

        return null;
    }

    public List<Machine> getMachines() {
        List<Machine> list = new ArrayList<>();
        String sql = "SELECT * FROM machine";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Machine machine = new Machine(
                        rs.getInt("id"),
                        rs.getString("nom_machine"),
                        rs.getString("type"),
                        rs.getString("etat"),
                        rs.getTimestamp("date_dernier_entretien"),
                        rs.getInt("prix_location_jour"),
                        rs.getString("marque"),
                        rs.getString("image") // can be null
                );
                list.add(machine);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving machine list:");
            e.printStackTrace();
        }

        return list;
    }
}
