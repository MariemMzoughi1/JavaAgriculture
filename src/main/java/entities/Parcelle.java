package entities;

import java.time.LocalDate;
import java.util.Objects;

public class Parcelle {

    private int id;
    private int cultureActuelleId; // ou Culture cultureActuelle;
    private String description;
    private String zone;
    private double superficie;
    private double prixDeLocation;
    private LocalDate dateDeLocation;
    private LocalDate dateDeFinLocation;
    private String etat;
    private String typeSol;
    private String image;

    public Parcelle() {}

    public Parcelle(int id, int cultureActuelleId, String description, String zone,
                    double superficie, double prixDeLocation, LocalDate dateDeLocation,
                    LocalDate dateDeFinLocation, String etat, String typeSol, String image) {
        this.id = id;
        this.cultureActuelleId = cultureActuelleId;
        this.description = description;
        this.zone = zone;
        this.superficie = superficie;
        this.prixDeLocation = prixDeLocation;
        this.dateDeLocation = dateDeLocation;
        this.dateDeFinLocation = dateDeFinLocation;
        this.etat = etat;
        this.typeSol = typeSol;
        this.image = image;
    }

    public Parcelle(int cultureActuelleId, String description, String zone,
                    double superficie, double prixDeLocation, LocalDate dateDeLocation,
                    LocalDate dateDeFinLocation, String etat, String typeSol, String image) {
        this.cultureActuelleId = cultureActuelleId;
        this.description = description;
        this.zone = zone;
        this.superficie = superficie;
        this.prixDeLocation = prixDeLocation;
        this.dateDeLocation = dateDeLocation;
        this.dateDeFinLocation = dateDeFinLocation;
        this.etat = etat;
        this.typeSol = typeSol;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCultureActuelleId() {
        return cultureActuelleId;
    }

    public void setCultureActuelleId(int cultureActuelleId) {
        this.cultureActuelleId = cultureActuelleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public double getPrixDeLocation() {
        return prixDeLocation;
    }

    public void setPrixDeLocation(double prixDeLocation) {
        this.prixDeLocation = prixDeLocation;
    }

    public LocalDate getDateDeLocation() {
        return dateDeLocation;
    }

    public void setDateDeLocation(LocalDate dateDeLocation) {
        this.dateDeLocation = dateDeLocation;
    }

    public LocalDate getDateDeFinLocation() {
        return dateDeFinLocation;
    }

    public void setDateDeFinLocation(LocalDate dateDeFinLocation) {
        this.dateDeFinLocation = dateDeFinLocation;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getTypeSol() {
        return typeSol;
    }

    public void setTypeSol(String typeSol) {
        this.typeSol = typeSol;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Parcelle{" +
                "id=" + id +
                ", cultureActuelleId=" + cultureActuelleId +
                ", description='" + description + '\'' +
                ", zone='" + zone + '\'' +
                ", superficie=" + superficie +
                ", prixDeLocation=" + prixDeLocation +
                ", dateDeLocation=" + dateDeLocation +
                ", dateDeFinLocation=" + dateDeFinLocation +
                ", etat='" + etat + '\'' +
                ", typeSol='" + typeSol + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parcelle parcelle)) return false;
        return id == parcelle.id &&
                cultureActuelleId == parcelle.cultureActuelleId &&
                Double.compare(parcelle.superficie, superficie) == 0 &&
                Double.compare(parcelle.prixDeLocation, prixDeLocation) == 0 &&
                Objects.equals(description, parcelle.description) &&
                Objects.equals(zone, parcelle.zone) &&
                Objects.equals(dateDeLocation, parcelle.dateDeLocation) &&
                Objects.equals(dateDeFinLocation, parcelle.dateDeFinLocation) &&
                Objects.equals(etat, parcelle.etat) &&
                Objects.equals(typeSol, parcelle.typeSol) &&
                Objects.equals(image, parcelle.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cultureActuelleId, description, zone, superficie,
                prixDeLocation, dateDeLocation, dateDeFinLocation, etat, typeSol, image);
    }
}
