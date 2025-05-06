package controllers;

import entities.Parcelle;
import services.ParcelleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ModifierParcelleController {

    @FXML private TextField tfId, tfCultureId, tfDescription, tfZone, tfSuperficie, tfPrix, tfEtat, tfTypeSol, tfImage;
    @FXML private DatePicker dpDateLocation, dpDateFinLocation;

    private final ParcelleService parcelleService = new ParcelleService();

    @FXML
    void modifierParcelle(MouseEvent event) {
        Parcelle p = new Parcelle(
                Integer.parseInt(tfCultureId.getText()),
                tfDescription.getText(),
                tfZone.getText(),
                Double.parseDouble(tfSuperficie.getText()),
                Double.parseDouble(tfPrix.getText()),
                dpDateLocation.getValue(),
                dpDateFinLocation.getValue(),
                tfEtat.getText(),
                tfTypeSol.getText(),
                tfImage.getText()
        );
        p.setId(Integer.parseInt(tfId.getText()));
        parcelleService.modifier(p);
        new Alert(Alert.AlertType.INFORMATION, "✅ Parcelle modifiée avec succès !").show();

        if (onModificationSuccess != null) {
            onModificationSuccess.run(); // ✅ rafraîchir
        }

        // ✅ Fermer la fenêtre
        Stage stage = (Stage) tfDescription.getScene().getWindow();
        stage.close();
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
    private Runnable onModificationSuccess;  // ✅ pour le callback

    public void setOnModificationSuccess(Runnable onModificationSuccess) {
        this.onModificationSuccess = onModificationSuccess;
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
            tfImage.setText(selectedFile.toURI().toString());  // Chemin au format URI
        }
    }
}
