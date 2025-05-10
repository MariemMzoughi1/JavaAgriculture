package com.esprit.Controllers;

import com.esprit.models.Culture;
import com.esprit.services.CultureService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ModifierCultureBackController {

    @FXML private TextField nomField;
    @FXML private TextField categorieField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePlantationPicker;
    @FXML private DatePicker dateRecoltePicker;
    @FXML private TextField saisonField;
    @FXML private TextField quantiteField;
    @FXML private TextField imageUrlField;
    @FXML private Button btnEnregistrer;

    private final CultureService cultureService = new CultureService();
    private Culture cultureActuelle;

    public void setCultureToModify(Culture culture) {
        this.cultureActuelle = culture;

        // Préremplir les champs
        nomField.setText(culture.getNom());
        categorieField.setText(culture.getCategorie());
        descriptionArea.setText(culture.getDescription());
        saisonField.setText(culture.getSaison());
        quantiteField.setText(String.valueOf(culture.getQuantite()));
        imageUrlField.setText(culture.getImage());
        datePlantationPicker.setValue(culture.getDatePlantation());
        dateRecoltePicker.setValue(culture.getDateRecolte());
    }

    @FXML
    private void handleModifier() {
        try {
            cultureActuelle.setNom(nomField.getText());
            cultureActuelle.setCategorie(categorieField.getText());
            cultureActuelle.setDescription(descriptionArea.getText());
            cultureActuelle.setSaison(saisonField.getText());
            cultureActuelle.setQuantite(Double.parseDouble(quantiteField.getText()));
            cultureActuelle.setImage(imageUrlField.getText());
            cultureActuelle.setDatePlantation(datePlantationPicker.getValue());
            cultureActuelle.setDateRecolte(dateRecoltePicker.getValue());

            cultureService.modifier(cultureActuelle);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Culture modifiée avec succès !");
            alert.showAndWait();

            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
