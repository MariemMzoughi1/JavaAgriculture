package Entites;

import java.util.Objects;

public class DetailCommande {

    private Long id;
    private Commande commande;
    private Produit produit;
    private int quantite;
    private double prix;
    private double soustotal;

    // --- Constructeur vide ---
    public DetailCommande() {}

    // --- Constructeur complet ---
    public DetailCommande(Long id, Commande commande, Produit produit, int quantite, double prix, double soustotal) {
        this.id = id;
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prix = prix;
        this.soustotal = soustotal;
    }

    // --- Constructeur simplifié (sans id) ---
    public DetailCommande(Commande commande, Produit produit, int quantite, double prix, double soustotal) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prix = prix;
        this.soustotal = soustotal;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getSoustotal() {
        return soustotal;
    }

    public void setSoustotal(double soustotal) {
        this.soustotal = soustotal;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "DetailCommande{" +
                "id=" + id +
                ", commande_id=" + (commande != null ? commande.getId() : null) +
                ", produit_id=" + (produit != null ? produit.getId() : null) +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", soustotal=" + soustotal +
                '}';
    }

    // --- equals & hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailCommande that)) return false;
        return quantite == that.quantite &&
                Double.compare(that.prix, prix) == 0 &&
                Double.compare(that.soustotal, soustotal) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(commande, that.commande) &&
                Objects.equals(produit, that.produit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commande, produit, quantite, prix, soustotal);
    }


}
