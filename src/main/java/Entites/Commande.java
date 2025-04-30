package Entites;

import java.time.LocalDateTime;
import java.util.Objects;

public class Commande {

    private Long id;
    private String etat;
    private LocalDateTime dateCommande;
    private double total;

    // --- Constructeur vide ---
    public Commande() {}

    // --- Constructeur complet ---
    public Commande(Long id, String etat, LocalDateTime dateCommande, double total) {
        this.id = id;
        this.etat = etat;
        this.dateCommande = dateCommande;
        this.total = total;
    }

    // --- Constructeur simplifié (sans id) ---
    public Commande(String etat, LocalDateTime dateCommande, double total) {
        this.etat = etat;
        this.dateCommande = dateCommande;
        this.total = total;
    }

    // --- Getters & Setters ---
    public int getId() {
        return Math.toIntExact(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }



    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", etat='" + etat + '\'' +
                ", dateCommande=" + dateCommande +
                ", total=" + total +
                '}';
    }

    // --- equals & hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commande commande)) return false;
        return Double.compare(commande.total, total) == 0 &&
                Objects.equals(id, commande.id) &&
                Objects.equals(etat, commande.etat) &&
                Objects.equals(dateCommande, commande.dateCommande);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, etat, dateCommande, total);
    }
}
