package Entites;

import java.util.Date;
import java.util.Objects;

public class Post {
    private int id;
    private String titre;
    private String contenu;
    private Integer auteurId; // Peut être null
    private Date date;
    private String image;
    private int likes;
    private int dislikes;

    public Post() {}

    public Post(int id, String titre, String contenu, Integer auteurId, Date date, String image, int likes, int dislikes) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.date = date;
        this.image = image;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Post(String titre, String contenu, Integer auteurId, Date date, String image, int likes, int dislikes) {
        this.titre = titre;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.date = date;
        this.image = image;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Post(String titre, String contenu) {
        this.titre = titre;
        this.contenu = contenu;
        this.date = new java.util.Date(); // ou LocalDateTime.now()
    }
    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int like) {
        this.likes = like;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislike) {
        this.dislikes = dislike;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", auteurId=" + auteurId +
                ", date=" + date +
                ", image='" + image + '\'' +
                ", like=" + likes +
                ", dislike=" + dislikes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post p)) return false;
        return id == p.id &&
                likes == p.likes &&
                dislikes == p.dislikes &&
                Objects.equals(titre, p.titre) &&
                Objects.equals(contenu, p.contenu) &&
                Objects.equals(auteurId, p.auteurId) &&
                Objects.equals(date, p.date) &&
                Objects.equals(image, p.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, contenu, auteurId, date, image, likes, dislikes);
    }
}
