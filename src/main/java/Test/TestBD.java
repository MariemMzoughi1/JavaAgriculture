/*package Test;

import Entites.Produit;
import Services.ProduitService;
import Utils.MyDB;

import java.time.LocalDate;

public class TestBD {
    public static void main(String[] args) {
        MyDB db = MyDB.getInstance();

        Produit p1 = new Produit("Tomate", "Tomate fraîche bio", "Légume", 1200, 50, 1, LocalDate.now());

        ProduitService psc = new ProduitService();
        psc.add(p1);

        System.out.println("Liste des produits :");
        psc.find().forEach(System.out::println); // suppose que la méthode find() retourne une List<Produit>

        System.out.println("Connexion DB utilisée : " + db);
    }
}
*/