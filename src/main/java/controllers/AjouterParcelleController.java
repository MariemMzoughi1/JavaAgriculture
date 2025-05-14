package controllers;

import entities.Culture;
import entities.Parcelle;
import javafx.scene.Node;
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

    @FXML private ComboBox<String> cbEtat;
    @FXML private ComboBox<String> cbTypeSol;
    @FXML private ComboBox<Culture> cbCulture;
    @FXML private TextField tfDescription;
    @FXML private TextField tfZone;
    @FXML private TextField tfSuperficie;
    @FXML private TextField tfPrix;
    @FXML private TextField tfImage;
    @FXML private DatePicker dpDateLocation;
    @FXML private DatePicker dpDateFinLocation;

    private final ParcelleService parcelleService = new ParcelleService();
    private final CultureService cultureService = new CultureService();
    private Runnable onAjoutSuccess;

    @FXML
    public void initialize() {
        // Remplir ComboBox Culture
        cbCulture.getItems().addAll(cultureService.afficher());

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

        // Valeurs fixes
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

            double superficieHa = Double.parseDouble(tfSuperficie.getText());
            double superficie = superficieHa * 10_000; // conversion ha → m²

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
            showSuccess("✅ Parcelle ajoutée avec succès !");

            if (onAjoutSuccess != null) {
                onAjoutSuccess.run();
            }

            Stage stage = (Stage) tfDescription.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showError("Veuillez entrer des nombres valides pour superficie et prix.");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("❌ " + message);
        alert.setHeaderText(null);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.show();
    }

    private void clearFields() {
        cbCulture.getSelectionModel().clearSelection();
        tfDescription.clear();
        tfZone.clear();
    }

    public void parcourirImage(ActionEvent actionEvent) {
    }

    public void retourAfficherParcelle(ActionEvent actionEvent) {
    }
    public void setOnAjoutSuccess(Runnable onAjoutSuccess) {
        this.onAjoutSuccess = onAjoutSuccess;
    }

    @FXML
    private void ajouterParcelle(ActionEvent event) {
        Parcelle p = new Parcelle();
        // ... Remplir les données depuis le formulaire

        ParcelleService ps = new ParcelleService();
        boolean ajoutReussi = ps.ajouter(p);

        if (ajoutReussi) {
            // Appel du callback pour rafraîchir la vue précédente
            if (onAjoutSuccess != null) {
                onAjoutSuccess.run();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Parcelle ajoutée !");
            alert.show();

            // Fermer la fenêtre
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Erreur lors de l'ajout !");
            alert.show();
        }
    }

    public void préremplirCulture(Culture c) {

    }
}
