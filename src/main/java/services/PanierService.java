package services;

import entities.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PanierService {

    private final ObservableList<Produit> panier = FXCollections.observableArrayList();
    private static PanierService instance;

    private PanierService() {}

    public static PanierService getInstance() {
        if (instance == null) {
            instance = new PanierService();
        }
        return instance;
    }

    public void ajouterProduit(Produit produit) {
        panier.add(produit);
    }

    public void supprimerProduit(Produit produit) {
        panier.removeIf(p -> p.getId() == produit.getId());
    }

    public void viderPanier() {
        panier.clear();
    }

    public ObservableList<Produit> getPanier() {
        return panier;  // <<< retourne la vraie liste observable !!
    }

    public double getTotal() {
        double total = 0;
        for (Produit produit : panier) {
            total += produit.getPrix_unitaire() * produit.getQuantite_stock();
        }
        return total;
    }
}
