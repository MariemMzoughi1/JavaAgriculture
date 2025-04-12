package Services;

import Entites.Produit;
import Interfaces.InterfaceCRUD;
import Utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements InterfaceCRUD<Produit> {
    Connection con;

    public ProduitService() {
        con = MyDB.getInstance().getCon();
    }

    @Override
    public void add(Produit produit) {
        String req = "INSERT INTO produit (nom, description, categorie, prix_unitaire, quantite_stock, image, agriculteur_id, date_ajout) VALUES ("
                + "'" + produit.getNom() + "', "
                + "'" + produit.getDescription() + "', "
                + "'" + produit.getCategorie() + "', "
                + produit.getPrix_unitaire() + ", "
                + produit.getQuantite_stock() + ", "
                + "'" + produit.getImage() + "', "
                + produit.getAgriculteur_id() + ", "
                + "'" + produit.getDate_ajout() + "'"
                + ")";
        try {
            Statement st = con.createStatement();
            st.executeUpdate(req);
            System.out.println("Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    @Override
    public void update(Produit produit) {
        // à implémenter
    }

    @Override
    public void delete(Produit produit) {
        // à implémenter
    }

    @Override
    public List<Produit> find() {
        String req = "SELECT * FROM produit";
        List<Produit> produits = new ArrayList<>();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setCategorie(rs.getString("categorie"));
                produit.setPrix_unitaire(rs.getInt("prix_unitaire"));
                produit.setQuantite_stock(rs.getInt("quantite_stock"));
                produit.setImage(rs.getString("image"));
                produit.setAgriculteur_id(rs.getInt("agriculteur_id"));
                produit.setDate_ajout(rs.getDate("date_ajout").toLocalDate());
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du find : " + e.getMessage());
        }
        return produits;
    }
}
