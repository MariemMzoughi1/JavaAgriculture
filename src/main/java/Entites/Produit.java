package Entites;

import java.time.LocalDate;
import java.util.Objects;

public class Produit {
    private int id;
    private String nom;
    private String description;
    private String categorie;
    private int prix_unitaire;
    private int quantite_stock;
    private String image;
    private int agriculteur_id;
    private LocalDate date_ajout;

    public Produit() {}

    // Constructeur complet
    public Produit(int id, String nom, String description, String categorie, int prix_unitaire, int quantite_stock, String image, int agriculteur_id, LocalDate date_ajout) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.prix_unitaire = prix_unitaire;
        this.quantite_stock = quantite_stock;
        this.image = image;
        this.agriculteur_id = agriculteur_id;
        this.date_ajout = date_ajout;
    }

    // Constructeur simplifié (sans id et image)
    public Produit(String nom, String description, String categorie, int prix_unitaire, int quantite_stock, int agriculteur_id, LocalDate date_ajout) {
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.prix_unitaire = prix_unitaire;
        this.quantite_stock = quantite_stock;
        this.agriculteur_id = agriculteur_id;
        this.date_ajout = date_ajout;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public int getPrix_unitaire() { return prix_unitaire; }
    public void setPrix_unitaire(int prix_unitaire) { this.prix_unitaire = prix_unitaire; }

    public int getQuantite_stock() { return quantite_stock; }
    public void setQuantite_stock(int quantite_stock) { this.quantite_stock = quantite_stock; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getAgriculteur_id() { return agriculteur_id; }
    public void setAgriculteur_id(int agriculteur_id) { this.agriculteur_id = agriculteur_id; }

    public LocalDate getDate_ajout() { return date_ajout; }
    public void setDate_ajout(LocalDate date_ajout) { this.date_ajout = date_ajout; }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prix_unitaire=" + prix_unitaire +
                ", quantite_stock=" + quantite_stock +
                ", image='" + image + '\'' +
                ", agriculteur_id=" + agriculteur_id +
                ", date_ajout=" + date_ajout +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produit produit)) return false;
        return id == produit.id &&
                prix_unitaire == produit.prix_unitaire &&
                quantite_stock == produit.quantite_stock &&
                agriculteur_id == produit.agriculteur_id &&
                Objects.equals(nom, produit.nom) &&
                Objects.equals(description, produit.description) &&
                Objects.equals(categorie, produit.categorie) &&
                Objects.equals(image, produit.image) &&
                Objects.equals(date_ajout, produit.date_ajout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, description, categorie, prix_unitaire, quantite_stock, image, agriculteur_id, date_ajout);
    }


}
