package Services;

import Entites.Post;
import Entites.User;
import Interfaces.InterfaceCRUD;
import Utils.MyDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PostService implements InterfaceCRUD<Post> {
    Connection con;
    private Map<Integer, String> userVotes = new HashMap<>();
    public PostService() {
        con = MyDB.getInstance().getCon();
    }

    @Override
    public void add(Post t) {
        // Détection bad words avant d'ajouter
        boolean hasBadWords = GeminiAPIService.contientBadWords(t.getContenu());
        if (hasBadWords) {
            System.out.println("❌ Impossible d'ajouter : contenu inapproprié détecté !");
            return;
        }

        // Vérification si c'est lié à l'agriculture
        boolean isRelatedToAgriculture = GeminiAPIService.estLieAAgriculture(t.getContenu());
        if (!isRelatedToAgriculture) {
            System.out.println("❌ Impossible d'ajouter : contenu non lié à l'agriculture !");
            return;
        }

        String req = "INSERT INTO post (titre, contenu, auteur_id, date, image, likes, dislikes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(req);
            ps.setString(1, t.getTitre());
            ps.setString(2, t.getContenu());

            if (t.getAuteurId() != null) {
                ps.setInt(3, t.getAuteurId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (t.getDate() != null) {
                ps.setTimestamp(4, new java.sql.Timestamp(t.getDate().getTime()));
            } else {
                ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            ps.setString(5, t.getImage());
            ps.setInt(6, t.getLikes());
            ps.setInt(7, t.getDislikes());

            ps.executeUpdate();
            System.out.println("✅ Post ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Post post) {
        // Détection bad words avant mise à jour
        boolean hasBadWords = GeminiAPIService.contientBadWords(post.getContenu());
        if (hasBadWords) {
            System.out.println("❌ Impossible de mettre à jour : contenu inapproprié détecté !");
            return;
        }

        // Vérification si c'est lié à l'agriculture
        boolean isRelatedToAgriculture = GeminiAPIService.estLieAAgriculture(post.getContenu());
        if (!isRelatedToAgriculture) {
            System.out.println("❌ Impossible de mettre à jour : contenu non lié à l'agriculture !");
            return;
        }

        String req = "UPDATE post SET titre = ?, contenu = ?, auteur_id = ?, date = ?, image = ?, likes = ?, dislikes = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(req);
            ps.setString(1, post.getTitre());
            ps.setString(2, post.getContenu());

            if (post.getAuteurId() != null) {
                ps.setInt(3, post.getAuteurId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (post.getDate() != null) {
                ps.setTimestamp(4, new Timestamp(post.getDate().getTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.setString(5, post.getImage());
            ps.setInt(6, post.getLikes());
            ps.setInt(7, post.getDislikes());
            ps.setInt(8, post.getId());

            int rowsUpdated = ps.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ Post mis à jour !" : "Aucun post trouvé avec cet ID.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Post post) {
        String req = "DELETE FROM post WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(req);
            ps.setInt(1, post.getId());
            int rowsDeleted = ps.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "✅ Post supprimé !" : "Aucun post trouvé avec cet ID.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // Une version personnalisée simple pour mise à jour sans contrôle
    public void updatePost(Post post) {
        String sql = "UPDATE post SET titre = ?, contenu = ?, image = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, post.getTitre());
            stmt.setString(2, post.getContenu());
            stmt.setString(3, post.getImage());
            stmt.setInt(4, post.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Post mis à jour avec succès !");
            } else {
                System.out.println("Aucune ligne mise à jour !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> find() {
        List<Post> posts = new ArrayList<>();
        String req = "SELECT * FROM post";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitre(rs.getString("titre"));
                p.setContenu(rs.getString("contenu"));
                p.setAuteurId(rs.getObject("auteur_id") != null ? rs.getInt("auteur_id") : null);
                p.setDate(rs.getTimestamp("date"));
                p.setImage(rs.getString("image"));
                p.setLikes(rs.getInt("likes"));
                p.setDislikes(rs.getInt("dislikes"));

                posts.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }

        return posts;
    }

    public void likePost(int postId, int userId) {
        String previousVote = userVotes.getOrDefault(postId, "none");

        try {
            if (previousVote.equals("like")) {
                // Annuler le like
                String sql = "UPDATE post SET likes = likes - 1 WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, postId);
                ps.executeUpdate();
                userVotes.remove(postId);
            } else if (previousVote.equals("dislike")) {
                // Enlever dislike et ajouter like
                String sql = "UPDATE post SET dislikes = dislikes - 1, likes = likes + 1 WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, postId);
                ps.executeUpdate();
                userVotes.put(postId, "like");
            } else {
                // Aucun vote précédent
                String sql = "UPDATE post SET likes = likes + 1 WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, postId);
                ps.executeUpdate();
                userVotes.put(postId, "like");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dislikePost(int postId, int userId) {
        String previousVote = userVotes.getOrDefault(postId, "none");

        try {
            if (previousVote.equals("dislike")) {
                // Annuler le dislike
                String sql = "UPDATE post SET dislikes = dislikes - 1 WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, postId);
                ps.executeUpdate();
                userVotes.remove(postId);
            } else if (previousVote.equals("like")) {
                // Enlever like et ajouter dislike
                String sql = "UPDATE post SET likes = likes - 1, dislikes = dislikes + 1 WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, postId);
                ps.executeUpdate();
                userVotes.put(postId, "dislike");
            } else {
                // Aucun vote précédent
                String sql = "UPDATE post SET dislikes = dislikes + 1 WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, postId);
                ps.executeUpdate();
                userVotes.put(postId, "dislike");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Post getPostById(int postId) {
        String req = "SELECT * FROM post WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitre(rs.getString("titre"));
                p.setContenu(rs.getString("contenu"));
                p.setAuteurId(rs.getObject("auteur_id") != null ? rs.getInt("auteur_id") : null);
                p.setDate(rs.getTimestamp("date"));
                p.setImage(rs.getString("image"));
                p.setLikes(rs.getInt("likes"));
                p.setDislikes(rs.getInt("dislikes"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du post : " + e.getMessage());
        }
        return null;
    }

    public User getAuteurByPostId(int postId) {
        String sql = "SELECT u.id, u.email, u.password, u.role, u.username " +
                "FROM post p JOIN user u ON p.auteur_id = u.id WHERE p.id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, postId);
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
            System.out.println("Erreur lors de la récupération de l'auteur : " + e.getMessage());
        }
        return null;
    }

}
