package controllers;

import entities.Culture;
import services.CultureService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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

    public void setOnModificationSuccess(Runnable callback) {
        this.onModificationSuccess = callback;
    }

    @FXML
    void modifierCulture(ActionEvent event) {
        try {
            if (tfNom.getText().isEmpty() || tfDescription.getText().isEmpty()
                    || tfSaison.getText().isEmpty() || tfQuantite.getText().isEmpty()
                    || tfCategorie.getText().isEmpty()
                    || dpDatePlantation.getValue() == null
                    || dpDateRecolte.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs obligatoires.");
                alert.show();
                return;
            }

            int id = Integer.parseInt(tfId.getText());
            String nom = tfNom.getText();
            String description = tfDescription.getText();
            String image = tfImage.getText();
            LocalDate datePlantation = dpDatePlantation.getValue();
            LocalDate dateRecolte = dpDateRecolte.getValue();
            String saison = tfSaison.getText();
            double quantite = Double.parseDouble(tfQuantite.getText());
            String categorie = tfCategorie.getText();

            Culture culture = new Culture(nom, description, image, datePlantation, dateRecolte, saison, quantite, categorie);
            culture.setId(id);

            cultureService.modifier(culture);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Culture modifiée avec succès !");
            alert.show();

            if (onModificationSuccess != null) {
                onModificationSuccess.run();
            }

            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Format de quantité invalide.");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Erreur lors de la modification : " + e.getMessage());
            alert.show();
        }
    }

    public void initData(Culture culture) {
        tfId.setText(String.valueOf(culture.getId()));
        tfNom.setText(culture.getNom());
        tfDescription.setText(culture.getDescription());
        tfImage.setText(culture.getImage());
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

        File selectedFile = fileChooser.showOpenDialog(tfImage.getScene().getWindow());
        if (selectedFile != null) {
            tfImage.setText(selectedFile.toURI().toString()); // Format URI compatible avec ImageView
        }
    }
}
