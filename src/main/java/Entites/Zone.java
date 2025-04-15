package Entites;

import java.util.Objects;

public class Zone {

    private int id;
    private float superficie_zone;
    private String nom_zone;
    private String localisation_zone;


    public Zone(){}

    public Zone(float superficie_zone, String nom_zone, String localisation_zone) {

        this.superficie_zone = superficie_zone;
        this.nom_zone = nom_zone;
        this.localisation_zone = localisation_zone;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public float getSuperficie_zone() {
        return superficie_zone;
    }

    public String getNom_zone() {
        return nom_zone;
    }

    public String getLocalisation_zone() {
        return localisation_zone;
    }



    public void setSuperficie_zone(float superficie_zone) {
        this.superficie_zone = superficie_zone;
    }

    public void setNom_zone(String nom_zone) {
        this.nom_zone = nom_zone;
    }

    public void setLocalisation_zone(String localisation_zone) {
        this.localisation_zone = localisation_zone;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id=" + id +
                ", superficie_zone=" + superficie_zone +
                ", nom_zone='" + nom_zone + '\'' +
                ", localisation_zone='" + localisation_zone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return id == zone.id && Float.compare(superficie_zone, zone.superficie_zone) == 0 && Objects.equals(nom_zone, zone.nom_zone) && Objects.equals(localisation_zone, zone.localisation_zone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, superficie_zone, nom_zone, localisation_zone);
    }
}