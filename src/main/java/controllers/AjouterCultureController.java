package controllers;

import entities.Culture;
import services.CultureService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    @FXML private ImageView imagePreview; // 🔁 Assure-toi que ce champ existe dans ton FXML

    private final CultureService cultureService = new CultureService();
    private File selectedImageFile;

    // 🔁 Chemin vers dossier Symfony
    private static final String SYMFONY_UPLOAD_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/images/";

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
            LocalDate datePlantation = dpDatePlantation.getValue();
            LocalDate dateRecolte = dpDateRecolte.getValue();
            String saison = cbSaison.getValue();
            String categorie = cbCategorie.getValue();

            if (nom.isEmpty() || description.isEmpty() || selectedImageFile == null
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

            // 🔁 Copie image dans Symfony
            String imageFileName = selectedImageFile.getName();
            File destFile = new File(SYMFONY_UPLOAD_DIR + imageFileName);
            Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 🔁 Création de la culture
            Culture culture = new Culture(nom, description, imageFileName, datePlantation, dateRecolte, saison, quantite, categorie);
            cultureService.ajouter(culture);

            showAlert(Alert.AlertType.INFORMATION, "✅ Culture ajoutée avec succès !");
            clearFields();

            if (onAjoutSuccess != null) onAjoutSuccess.run();

            ((Stage) tfNom.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide. Veuillez saisir un nombre.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void parcourirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(tfNom.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            tfImage.setText(file.getName()); // 👈 seulement le nom du fichier
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void retourAfficherParcelle(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(null);
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
        imagePreview.setImage(null);
    }
}
