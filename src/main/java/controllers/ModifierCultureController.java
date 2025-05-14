package controllers;

import entities.Culture;
import services.CultureService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

public class ModifierCultureController {

    @FXML private TextField tfId;
    @FXML private TextField tfNom;
    @FXML private TextField tfDescription;
    @FXML private TextField tfImage;
    @FXML private DatePicker dpDatePlantation;
    @FXML private DatePicker dpDateRecolte;
    @FXML private TextField tfSaison;
    @FXML private TextField tfQuantite;
    @FXML private TextField tfCategorie;

    private final CultureService cultureService = new CultureService();
    private Runnable onModificationSuccess;
    private File selectedImageFile = null;

    // 📁 Adapter le chemin à votre environnement local
    private static final String SYMFONY_UPLOAD_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/images/";

    public void setOnModificationSuccess(Runnable callback) {
        this.onModificationSuccess = callback;
    }

    @FXML
    void modifierCulture(ActionEvent event) {
        try {
            if (tfNom.getText().isEmpty() || tfDescription.getText().isEmpty()
                    || tfSaison.getText().isEmpty() || tfQuantite.getText().isEmpty()
                    || tfCategorie.getText().isEmpty()
                    || dpDatePlantation.getValue() == null || dpDateRecolte.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            int id = Integer.parseInt(tfId.getText());
            String nom = tfNom.getText();
            String description = tfDescription.getText();
            String imageName = tfImage.getText(); // Par défaut
            LocalDate datePlantation = dpDatePlantation.getValue();
            LocalDate dateRecolte = dpDateRecolte.getValue();
            String saison = tfSaison.getText();
            double quantite = Double.parseDouble(tfQuantite.getText());
            String categorie = tfCategorie.getText();

            if (quantite <= 0) {
                showAlert(Alert.AlertType.WARNING, "La quantité doit être supérieure à 0.");
                return;
            }

            if (datePlantation.isAfter(dateRecolte)) {
                showAlert(Alert.AlertType.WARNING, "La date de plantation doit être avant la date de récolte.");
                return;
            }

            // 📥 Copie de l'image dans Symfony si une nouvelle image est sélectionnée
            if (selectedImageFile != null) {
                String fileName = selectedImageFile.getName();
                Path destination = Paths.get(SYMFONY_UPLOAD_DIR + fileName);
                Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                imageName = fileName; // Enregistrer seulement le nom de fichier
            }

            Culture culture = new Culture(nom, description, imageName, datePlantation, dateRecolte, saison, quantite, categorie);
            culture.setId(id);

            cultureService.modifier(culture);
            showAlert(Alert.AlertType.INFORMATION, "✅ Culture modifiée avec succès !");

            if (onModificationSuccess != null) {
                onModificationSuccess.run();
            }

            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Format de quantité invalide.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur de copie de l'image : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur : " + e.getMessage());
        }
    }

    public void initData(Culture culture) {
        tfId.setText(String.valueOf(culture.getId()));
        tfNom.setText(culture.getNom());
        tfDescription.setText(culture.getDescription());
        tfImage.setText(culture.getImage()); // image = nom du fichier
        dpDatePlantation.setValue(culture.getDatePlantation());
        dpDateRecolte.setValue(culture.getDateRecolte());
        tfSaison.setText(culture.getSaison());
        tfQuantite.setText(String.valueOf(culture.getQuantite()));
        tfCategorie.setText(culture.getCategorie());
    }

    @FXML
    private void retourAfficherParcelle(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void parcourirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(tfImage.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            tfImage.setText(file.getName()); // Affiche seulement le nom de fichier
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
