package Entites;

import java.util.Date;
import java.util.Objects;

public class Commentaire {
    private int id;
    private int postId;
    private String contenu;
    private Integer auteurId; // Peut être null
    private Date date;

    public Commentaire() {
    }

    public Commentaire(int id, int postId, String contenu, Integer auteurId, Date date) {
        this.id = id;
        this.postId = postId;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.date = date;
    }

    public Commentaire(int postId, String contenu, Integer auteurId, Date date) {
        this.postId = postId;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Integer getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(Integer auteurId) {
        this.auteurId = auteurId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", postId=" + postId +
                ", contenu='" + contenu + '\'' +
                ", auteurId=" + auteurId +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commentaire)) return false;
        Commentaire that = (Commentaire) o;
        return id == that.id && postId == that.postId && Objects.equals(contenu, that.contenu) && Objects.equals(auteurId, that.auteurId) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, contenu, auteurId, date);
    }
}
