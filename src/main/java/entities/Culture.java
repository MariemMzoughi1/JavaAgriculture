package entities;
import java.time.LocalDate;


public class Culture {

    private int id;
    private String nom;
    private String description;
    private String image;
    private LocalDate datePlantation;
    private LocalDate dateRecolte;
    private String saison;
    private double quantite;
    private String categorie;

    public Culture() {}

    public Culture(String nom, String description, String image, LocalDate datePlantation,
                   LocalDate dateRecolte, String saison, double quantite, String categorie) {
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.datePlantation = datePlantation;
        this.dateRecolte = dateRecolte;
        this.saison = saison;
        this.quantite = quantite;
        this.categorie = categorie;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public LocalDate getDatePlantation() {
        return datePlantation;
    }

    public LocalDate getDateRecolte() {
        return dateRecolte;
    }

    public String getSaison() {
        return saison;
    }

    public double getQuantite() {
        return quantite;
    }

    public String getCategorie() {
        return categorie;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDatePlantation(LocalDate datePlantation) {
        this.datePlantation = datePlantation;
    }

    public void setDateRecolte(LocalDate dateRecolte) {
        this.dateRecolte = dateRecolte;
    }

    public void setSaison(String saison) {
        this.saison = saison;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Culture{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", datePlantation=" + datePlantation +
                ", dateRecolte=" + dateRecolte +
                ", saison='" + saison + '\'' +
                ", quantite=" + quantite +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}
