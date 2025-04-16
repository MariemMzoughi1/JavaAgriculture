package Entites;

import java.sql.Timestamp;

//db table name: machine
//fields: ids, nom_machine:varchar, type:varchar, etat:varchar, date_dernier_entretien:datetime, prix_location_jour:int, marque:varchar, image:varchar
public class Machine {
    private int id;
    private String name;
    private String type;
    private String etat;
    private Timestamp dateLastCheckup;
    private int pricePerDay;
    private String brand;
    private String image_url;//nullable btw

    public Machine(int id, String name, String type, String etat, Timestamp dateLastCheckup, int pricePerDay, String brand, String image_url) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.etat = etat;
        this.dateLastCheckup = dateLastCheckup;
        this.pricePerDay = pricePerDay;
        this.brand = brand;
        this.image_url = image_url;
    }

    public Machine(String name, String type, String etat, Timestamp dateLastCheckup, int pricePerDay, String brand, String image_url) {
        this.name = name;
        this.type = type;
        this.etat = etat;
        this.dateLastCheckup = dateLastCheckup;
        this.pricePerDay = pricePerDay;
        this.brand = brand;
        this.image_url = image_url;
    }

    public Machine() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Timestamp getDateLastCheckup() {
        return dateLastCheckup;
    }

    public void setDateLastCheckup(Timestamp dateLastCheckup) {
        this.dateLastCheckup = dateLastCheckup;
    }

    public int getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(int pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
