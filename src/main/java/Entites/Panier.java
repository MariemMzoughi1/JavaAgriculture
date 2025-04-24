package Entites;

public class Panier {

        private int id;
        private Produit produit;
        private int quantite;

        public Panier() {
        }

        public Panier(int id, Produit produit, int quantite) {
            this.id = id;
            this.produit = produit;
            this.quantite = quantite;
        }

        public Panier(Produit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
        }

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



