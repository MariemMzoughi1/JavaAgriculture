package Repository;

import entities.Produit;

import java.util.List;

public class PanierRepository {


    public void viderPanier(int userId) {
    }

    public List<entities.Panier> findPanierByUser(int userId) {
        return List.of();
    }

    public class Panier {
        private int id;
        private Produit produit;
        private int quantite;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
    }





}
