package Services;

import Entites.User;
import Utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Connection con;

    public UserService() {
        this.con = MyDB.getInstance().getCon();
    }

    // GET by Email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("username")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // CREATE
    public boolean ajouterUser(User user) {
        String sql = "INSERT INTO user (email, password, role, username) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getRole());
            pst.setString(4, user.getUsername());
            pst.executeUpdate();
            System.out.println("Utilisateur ajouté !");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // READ
    public List<User> afficherUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("username")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // UPDATE
    public void modifierUser(User user) {
        String sql = "UPDATE user SET email = ?, password = ?, role = ?, username = ? WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getRole());
            pst.setString(4, user.getUsername());
            pst.setInt(5, user.getId());
            pst.executeUpdate();
            System.out.println("Utilisateur modifié !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void supprimerUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Utilisateur supprimé !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // GET by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("username")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
