package controles;

import Entites.Produit;

public class PanierItem {
    private Produit produit;
    private int quantite;

    public PanierItem(Produit produit) {
        this.produit = produit;
        this.quantite = 1;  // Par défaut, 1 produit ajouté
    }

    public Produit getProduit() {
        return produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getTotalPrix() {
        return produit.getPrix_unitaire() * quantite;
    }
}
