package controllers;

import entities.Parcelle;
import services.ParcelleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class ModifierParcelleController {

    @FXML private TextField tfId;
    @FXML private TextField tfCultureId;
    @FXML private TextField tfDescription;
    @FXML private TextField tfZone;
    @FXML private TextField tfSuperficie;
    @FXML private TextField tfPrix;
    @FXML private TextField tfEtat;
    @FXML private TextField tfTypeSol;
    @FXML private TextField tfImage;
    @FXML private DatePicker dpDateLocation;
    @FXML private DatePicker dpDateFinLocation;

    private final ParcelleService parcelleService = new ParcelleService();
    private Runnable onModificationSuccess;

    public void setOnModificationSuccess(Runnable callback) {
        this.onModificationSuccess = callback;
    }

    @FXML
    void modifierParcelle(MouseEvent event) {
        try {
            // ⚠️ Vérification des champs obligatoires
            if (tfCultureId.getText().isEmpty() || tfDescription.getText().isEmpty() || tfZone.getText().isEmpty()
                    || tfSuperficie.getText().isEmpty() || tfPrix.getText().isEmpty()
                    || tfEtat.getText().isEmpty() || tfTypeSol.getText().isEmpty()
                    || dpDateLocation.getValue() == null || dpDateFinLocation.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            int cultureId = Integer.parseInt(tfCultureId.getText());
            double superficie = Double.parseDouble(tfSuperficie.getText());
            double prix = Double.parseDouble(tfPrix.getText());
            LocalDate dateDebut = dpDateLocation.getValue();
            LocalDate dateFin = dpDateFinLocation.getValue();

            if (dateDebut.isAfter(dateFin)) {
                showAlert(Alert.AlertType.WARNING, "La date de début doit être avant la date de fin.");
                return;
            }

            Parcelle p = new Parcelle(
                    cultureId,
                    tfDescription.getText(),
                    tfZone.getText(),
                    superficie,
                    prix,
                    dateDebut,
                    dateFin,
                    tfEtat.getText(),
                    tfTypeSol.getText(),
                    tfImage.getText()
            );
            p.setId(Integer.parseInt(tfId.getText()));

            parcelleService.modifier(p);

            showAlert(Alert.AlertType.INFORMATION, "✅ Parcelle modifiée avec succès !");

            if (onModificationSuccess != null) {
                onModificationSuccess.run();
            }

            Stage stage = (Stage) tfDescription.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Superficie ou prix invalide.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Erreur : " + e.getMessage());
        }
    }

    public void setParcelle(Parcelle p) {
        tfId.setText(String.valueOf(p.getId()));
        tfCultureId.setText(String.valueOf(p.getCultureActuelleId()));
        tfDescription.setText(p.getDescription());
        tfZone.setText(p.getZone());
        tfSuperficie.setText(String.valueOf(p.getSuperficie()));
        tfPrix.setText(String.valueOf(p.getPrixDeLocation()));
        dpDateLocation.setValue(p.getDateDeLocation());
        dpDateFinLocation.setValue(p.getDateDeFinLocation());
        tfEtat.setText(p.getEtat());
        tfTypeSol.setText(p.getTypeSol());
        tfImage.setText(p.getImage());
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
            tfImage.setText(selectedFile.toURI().toString());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
