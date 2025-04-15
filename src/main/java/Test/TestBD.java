package Test;

import Entites.Zone;
import Services.ZoneService;
import Utils.MyDB;

public class TestBD {
    public static void main(String[] args) {
        MyDB db = MyDB.getInstance();


        Zone z1 = new Zone(200,"monzone","tunis");


        ZoneService zsc = new ZoneService();

        //zsc.add(z1);




        System.out.println(zsc.find());
        MyDB db2 = MyDB.getInstance();
        //MyDB db3 = MyDB.getInstance();
        //MyDB db2 = new MyDB();
        //MyDB db3 = new MyDB();

        //System.out.println(db);
        System.out.println(db2);
        //System.out.println(db3);
    }
}