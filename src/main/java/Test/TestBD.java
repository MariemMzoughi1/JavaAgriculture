package Test;

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
        //psc.add(p2);
        // psc.add(p3);

        System.out.println(psc.find());
        //MyDB db2 = MyDB.getInstance();
        //MyDB db3 = MyDB.getInstance();
        //MyDB db2 = new MyDB();
        //MyDB db3 = new MyDB();

        System.out.println(db);
        //System.out.println(db2);
        //System.out.println(db3);
    }
}
