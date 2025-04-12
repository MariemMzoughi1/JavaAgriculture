package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {
    String url = "jdbc:mysql://localhost:3306/devharvest";
    String user = "root";
    String password = "";
    private Connection con;
    private static MyDB instanc;

    private MyDB() {
        try {
            this.con = DriverManager.getConnection(this.url, this.user, this.password);
            System.out.println("Connected to database");
        } catch (SQLException var2) {
            System.out.println(var2.getMessage());
        }

    }

    public static MyDB getInstance() {
        if (instanc == null) {
            instanc = new MyDB();
        }

        return instanc;
    }

    public Connection getCon() {
        return this.con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
}
