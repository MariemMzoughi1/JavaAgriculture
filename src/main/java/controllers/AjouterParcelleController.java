package controllers;

import entities.Culture;
import entities.Parcelle;
import services.CultureService;
import services.ParcelleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class AjouterParcelleController {
    @FXML private ComboBox<String> cbEtat, cbTypeSol;
    private Runnable onAjoutSuccess;

    @FXML private ComboBox<Culture> cbCulture;
    @FXML private TextField tfDescription, tfZone, tfSuperficie, tfPrix, tfEtat, tfTypeSol, tfImage;
    @FXML private DatePicker dpDateLocation, dpDateFinLocation;

    private final ParcelleService parcelleService = new ParcelleService();
    private final CultureService cultureService = new CultureService();

    @FXML
    public void initialize() {
        // Charger les cultures
        cbCulture.getItems().addAll(cultureService.afficher());

        // Affichage nom seulement
        cbCulture.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Culture item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        cbCulture.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Culture item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        // Valeurs pour les ComboBox fixes
        cbEtat.getItems().addAll("bon", "moyen", "mauvais");
        cbTypeSol.getItems().addAll("argileux", "sableux", "limoneux", "calcaire");
    }


    @FXML
    void ajouterParcelle(MouseEvent event) {
        try {
            Culture selectedCulture = cbCulture.getValue();
            if (selectedCulture == null) {
                showError("Veuillez sélectionner une culture.");
                return;
            }

            double superficie = Double.parseDouble(tfSuperficie.getText());
            double prix = Double.parseDouble(tfPrix.getText());
            LocalDate dateLocation = dpDateLocation.getValue();
            LocalDate dateFin = dpDateFinLocation.getValue();

            if (dateLocation == null || dateFin == null) {
                showError("Les dates ne doivent pas être nulles.");
                return;
            }

            if (dateLocation.isAfter(dateFin)) {
                showError("La date de début doit être avant la date de fin.");
                return;
            }

            Parcelle p = new Parcelle(
                    selectedCulture.getId(),
                    tfDescription.getText(),
                    tfZone.getText(),
                    superficie,
                    prix,
                    dateLocation,
                    dateFin,
                    cbEtat.getValue(),
                    cbTypeSol.getValue(),
                    tfImage.getText()
            );

            parcelleService.ajouter(p);
            new Alert(Alert.AlertType.INFORMATION, "✅ Parcelle ajoutée avec succès !").show();

            // ✅ Rafraîchir la liste
            if (onAjoutSuccess != null) {
                onAjoutSuccess.run();
            }

            // ✅ Fermer la fenêtre
            Stage stage = (Stage) tfDescription.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showError("Veuillez entrer des nombres valides pour superficie et prix.");
        }
    }
    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, "❌ " + message).show();
    }

    private void clearFields() {
        cbCulture.getSelectionModel().clearSelection();
        tfDescription.clear();
        tfZone.clear();
        tfSuperficie.clear();
        tfPrix.clear();
        tfImage.clear();
        dpDateLocation.setValue(null);
        dpDateFinLocation.setValue(null);
        cbEtat.getSelectionModel().clearSelection();
        cbTypeSol.getSelectionModel().clearSelection();

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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(tfImage.getScene().getWindow());
        if (selectedFile != null) {
            tfImage.setText(selectedFile.toURI().toString());  // Important pour ImageView
        }
    }
    public void setOnAjoutSuccess(Runnable onAjoutSuccess) {
        this.onAjoutSuccess = onAjoutSuccess;
    }

}
