package Services;

import Entites.Commentaire;
import Interfaces.InterfaceCRUD;
import Utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentaireService implements InterfaceCRUD<Commentaire> {
    private Connection con;

    public CommentaireService() {
        con = MyDB.getInstance().getCon();
    }

    @Override
    public void add(Commentaire c) {
        try {
            // Check for bad words using Gemini API
            if (GeminiAPIService.contientBadWords(c.getContenu())) {
                System.out.println("❌ Commentaire inapproprié détecté !");
                throw new IllegalArgumentException("Le contenu contient des mots ou expressions inappropriés.");
            }

            String req = "INSERT INTO commentaire (post_id, contenu, auteur_id, date) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(req);
            ps.setInt(1, c.getPostId());
            ps.setString(2, c.getContenu());
            if (c.getAuteurId() != null) {
                ps.setInt(3, c.getAuteurId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, new Timestamp(c.getDate().getTime()));
            ps.executeUpdate();
            System.out.println("✅ Commentaire ajouté !");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
            throw e; // Rethrow to be handled by the controller
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Commentaire c) {
        try {
            // Check for bad words using Gemini API
            if (GeminiAPIService.contientBadWords(c.getContenu())) {
                System.out.println("❌ Commentaire inapproprié détecté !");
                throw new IllegalArgumentException("Le contenu contient des mots ou expressions inappropriés.");
            }

            String req = "UPDATE commentaire SET contenu = ?, auteur_id = ?, date = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(req);
            ps.setString(1, c.getContenu());
            if (c.getAuteurId() != null) {
                ps.setInt(2, c.getAuteurId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setTimestamp(3, new Timestamp(c.getDate().getTime()));
            ps.setInt(4, c.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Commentaire mis à jour !");
            } else {
                System.out.println("⚠️ Aucun commentaire trouvé avec cet ID.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
            throw e; // Rethrow to be handled by the controller
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Commentaire c) {
        String req = "DELETE FROM commentaire WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(req);
            ps.setInt(1, c.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("🗑️ Commentaire supprimé !");
            } else {
                System.out.println("⚠️ Aucun commentaire trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Commentaire> find() {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setPostId(rs.getInt("post_id"));
                c.setContenu(rs.getString("contenu"));
                int auteurId = rs.getInt("auteur_id");
                c.setAuteurId(rs.wasNull() ? null : auteurId);
                c.setDate(rs.getTimestamp("date"));
                commentaires.add(c);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return commentaires;
    }

    public void ajouterCommentaire(Commentaire c) {
        try {
            // Check for bad words using Gemini API
            if (GeminiAPIService.contientBadWords(c.getContenu())) {
                throw new IllegalArgumentException("Le contenu contient des mots ou expressions inappropriés.");
            }

            String sql = "INSERT INTO commentaire (post_id, contenu, auteur_id, date) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, c.getPostId());
            ps.setString(2, c.getContenu());
            if (c.getAuteurId() != null)
                ps.setInt(3, c.getAuteurId());
            else
                ps.setNull(3, Types.INTEGER);
            ps.setDate(4, new java.sql.Date(c.getDate().getTime()));
            ps.executeUpdate();
        } catch (IllegalArgumentException e) {
            throw e; // Rethrow to be handled by the controller
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Commentaire> getCommentairesParPostId(int postId) {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE post_id = ? ORDER BY date DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commentaire c = new Commentaire(
                        rs.getInt("id"),
                        rs.getInt("post_id"),
                        rs.getString("contenu"),
                        rs.getInt("auteur_id"),
                        rs.getDate("date")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void supprimerCommentaire(int id) {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierCommentaire(Commentaire c) {
        try {
            // Check for bad words using Gemini API
            if (GeminiAPIService.contientBadWords(c.getContenu())) {
                throw new IllegalArgumentException("Le contenu contient des mots ou expressions inappropriés.");
            }

            String sql = "UPDATE commentaire SET contenu = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getContenu());
            ps.setInt(2, c.getId());
            ps.executeUpdate();
        } catch (IllegalArgumentException e) {
            throw e; // Rethrow to be handled by the controller
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}