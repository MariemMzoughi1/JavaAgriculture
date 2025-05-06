package controllers;

import entities.Culture;
import services.CultureService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

public class AjouterCultureController {

    @FXML private TextField tfNom;
    @FXML private TextField tfDescription;
    @FXML private TextField tfImage;
    @FXML private DatePicker dpDatePlantation;
    @FXML private DatePicker dpDateRecolte;
    @FXML private TextField tfQuantite;
    @FXML private ComboBox<String> cbSaison;
    @FXML private ComboBox<String> cbCategorie;

    private CultureService cultureService = new CultureService();

    private Runnable onAjoutSuccess;

    @FXML
    public void initialize() {
        cbSaison.getItems().addAll("été", "printemps", "automne", "hiver");
        cbCategorie.getItems().addAll("légume", "fruit", "céréales");
    }

    public void setOnAjoutSuccess(Runnable onAjoutSuccess) {
        this.onAjoutSuccess = onAjoutSuccess;
    }

    @FXML
    void ajouterCulture(MouseEvent event) {
        try {
            String nom = tfNom.getText();
            String description = tfDescription.getText();
            String image = tfImage.getText();
            LocalDate datePlantation = dpDatePlantation.getValue();
            LocalDate dateRecolte = dpDateRecolte.getValue();
            String saison = cbSaison.getValue();
            String categorie = cbCategorie.getValue();

            // Contrôle de saisie
            if (nom.isEmpty() || description.isEmpty() || image.isEmpty()
                    || datePlantation == null || dateRecolte == null
                    || saison == null || categorie == null
                    || tfQuantite.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                return;
            }

            if (!Arrays.asList("été", "printemps", "automne", "hiver").contains(saison)) {
                showAlert(Alert.AlertType.WARNING, "Saison invalide.");
                return;
            }

            if (!Arrays.asList("légume", "fruit", "céréales").contains(categorie)) {
                showAlert(Alert.AlertType.WARNING, "Catégorie invalide.");
                return;
            }

            double quantite = Double.parseDouble(tfQuantite.getText());
            if (quantite <= 0) {
                showAlert(Alert.AlertType.WARNING, "La quantité doit être supérieure à 0.");
                return;
            }

            if (datePlantation.isAfter(dateRecolte)) {
                showAlert(Alert.AlertType.WARNING, "La date de plantation doit être avant la date de récolte.");
                return;
            }

            Culture culture = new Culture(nom, description, image, datePlantation, dateRecolte, saison, quantite, categorie);
            cultureService.ajouter(culture);

            showAlert(Alert.AlertType.INFORMATION, "✅ Culture ajoutée avec succès !");

            clearFields();
            if (onAjoutSuccess != null) onAjoutSuccess.run();

            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide. Veuillez saisir un nombre.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.show();
    }

    private void clearFields() {
        tfNom.clear();
        tfDescription.clear();
        tfImage.clear();
        dpDatePlantation.setValue(null);
        dpDateRecolte.setValue(null);
        tfQuantite.clear();
        cbSaison.setValue(null);
        cbCategorie.setValue(null);
    }

    @FXML
    private void retourAfficherParcelle(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void parcourirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        // Filtre pour images seulement
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(tfNom.getScene().getWindow());
        if (selectedFile != null) {
            tfImage.setText(selectedFile.toURI().toString()); // important : utiliser URI pour chargement JavaFX Image
        }
    }
}
