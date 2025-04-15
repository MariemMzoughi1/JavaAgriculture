package Entites;

import java.util.Objects;

public class Grange {
    private int id;
    private String type_grange;
    private float capacite;

    public Grange(){}

    public Grange(String type_grange, float capacite) {
        this.type_grange = type_grange;
        this.capacite = capacite;
    }

    public String getType_grange() {
        return type_grange;
    }

    public void setType_grange(String type_grange) {
        this.type_grange = type_grange;
    }

    public float getCapacite() {
        return capacite;
    }

    public void setCapacite(float capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Grange{" +
                "id=" + id +
                ", type_grange='" + type_grange + '\'' +
                ", capacite=" + capacite +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Grange grange = (Grange) o;
        return id == grange.id && Float.compare(capacite, grange.capacite) == 0 && Objects.equals(type_grange, grange.type_grange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type_grange, capacite);
    }

}