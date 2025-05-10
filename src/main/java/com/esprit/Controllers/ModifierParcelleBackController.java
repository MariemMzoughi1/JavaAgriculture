package com.esprit.Controllers;

import com.esprit.models.Parcelle;
import com.esprit.services.ParcelleService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierParcelleBackController {

    @FXML private TextField zoneField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField superficieField;
    @FXML private TextField prixLocationField;
    @FXML private DatePicker dateLocationPicker;
    @FXML private DatePicker dateFinLocationPicker;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private ComboBox<String> solComboBox;
    @FXML private TextField imageField;
    @FXML private Button saveButton;

    private final ParcelleService parcelleService = new ParcelleService();
    private Parcelle parcelleToModify;

    public void initialize() {
        etatComboBox.getItems().addAll("bon", "moyen", "mauvais");
        solComboBox.getItems().addAll("argileux", "sableux", "limoneux", "calcaire");

        saveButton.setOnAction(e -> enregistrerModifications());
    }

    public void setParcelleToModify(Parcelle p) {
        this.parcelleToModify = p;
        zoneField.setText(p.getZone());
        descriptionArea.setText(p.getDescription());
        superficieField.setText(String.valueOf(p.getSuperficie()));
        prixLocationField.setText(String.valueOf(p.getPrixDeLocation()));
        dateLocationPicker.setValue(p.getDateDeLocation());
        dateFinLocationPicker.setValue(p.getDateDeFinLocation());
        etatComboBox.setValue(p.getEtat());
        solComboBox.setValue(p.getTypeSol());
        imageField.setText(p.getImage());
    }

    private void enregistrerModifications() {
        try {
            parcelleToModify.setZone(zoneField.getText());
            parcelleToModify.setDescription(descriptionArea.getText());
            parcelleToModify.setSuperficie(Double.parseDouble(superficieField.getText()));
            parcelleToModify.setPrixDeLocation(Double.parseDouble(prixLocationField.getText()));
            parcelleToModify.setDateDeLocation(dateLocationPicker.getValue());
            parcelleToModify.setDateDeFinLocation(dateFinLocationPicker.getValue());
            parcelleToModify.setEtat(etatComboBox.getValue());
            parcelleToModify.setTypeSol(solComboBox.getValue());
            parcelleToModify.setImage(imageField.getText());

            parcelleService.modifier(parcelleToModify);

            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
        }
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
