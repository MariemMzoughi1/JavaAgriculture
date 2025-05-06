package services;

import entities.User;
import Database.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (email, password, role, reset_token) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getReset_token());
            stmt.executeUpdate();
            System.out.println("✅ User added successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void editUser(User user) {
        String sql = "UPDATE users SET email = ?, password = ?, role = ?, reset_token = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getReset_token());
            stmt.setInt(5, user.getId());
            stmt.executeUpdate();
            System.out.println("✏️ User updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
            System.out.println("🗑️ User deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("reset_token")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DbConnection.getInstance().getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("reset_token")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("reset_token")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET email = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getId());

            ps.executeUpdate();
            System.out.println("✅ Utilisateur mis à jour avec succès");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    public boolean verifyResetToken(String email, String token) {
        String sql = "SELECT * FROM users WHERE email = ? AND reset_token = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, token);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // ✅ Token matches if a result is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String email, String newHashedPassword) {
        String sql = "UPDATE users SET password = ?, reset_token = NULL WHERE email = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHashedPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // ✅ Password updated successfully if at least 1 row changed
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateResetToken(String email, String token) {
        String sql = "UPDATE users SET reset_token = ? WHERE email = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setString(2, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getEmailById(int id) {
        String sql = "SELECT email FROM users WHERE id = ?";
        try (Connection conn = DbConnection.getInstance().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Email inconnu";
    }


}
