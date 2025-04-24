package Services;



import Entites.Produit;

import java.util.ArrayList;
import java.util.List;

public class PanierService {

    private final List<Produit> panier = new ArrayList<>();

    public void ajouterProduit(Produit produit) {
        panier.add(produit);
    }

    public void supprimerProduit(Produit produit) {
        panier.removeIf(p -> p.getId() == produit.getId());
    }

    public void viderPanier() {
        panier.clear();
    }

    public List<Produit> getPanier() {
        return new ArrayList<>(panier);
    }

    public double getTotal() {
        double total = 0;
        for (Produit produit : panier) {
            total += produit.getPrix_unitaire(); // Supposons que Produit a un getPrix()
        }
        return total;
    }
}
