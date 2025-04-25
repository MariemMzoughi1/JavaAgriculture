package Controlles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Entites.Zone;
import Services.ZoneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class AjouterZone {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField localisationTextField;

    @FXML
    private TextField nomzoneTextField;

    @FXML
    private TextField superficieTextField;

    @FXML
    private Text imagePathText;

    private File selectedImageFile;


    private double latitude;
    private double longitude;

    @FXML
    public void initialize() {

    }

    @FXML
    void AjouterZone(ActionEvent event) {
        String superficieStr = superficieTextField.getText();
        String nomZone = nomzoneTextField.getText();
        String localisation = localisationTextField.getText();
        String image = imagePathText.getText();  // Récupération du chemin de l'image

        if (superficieStr.isEmpty() || nomZone.isEmpty() || localisation.isEmpty() || image.isEmpty()) {
            showAlert("Tous les champs sont obligatoires.");
            return;
        }

        float superficie;
        try {
            superficie = Float.parseFloat(superficieStr);
            if (superficie <= 0) {
                showAlert("La superficie doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("La superficie doit être un nombre valide.");
            return;
        }

        if (nomZone.length() < 3) {
            showAlert("Le nom de la zone doit contenir au moins 3 caractères.");
            return;
        }

        Zone zone = new Zone(superficie, nomZone, localisation, image);
        zone.setImage(image);  // Utilisation de l'attribut "image" dans la classe Zone
        ZoneService zoneservice = new ZoneService();

        try {
            zoneservice.add(zone);  // Méthode d'ajout dans la base de données

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Zone ajoutée avec succès !");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherZone.fxml"));
            Parent root = loader.load();
            AfficherZone afficherZone = loader.getController();
            afficherZone.setSuperficie(superficie);
            afficherZone.setNomdezone(nomZone);
            afficherZone.setLocalisation(localisation);
            afficherZone.setImage(image);  // Passer le chemin de l'image au contrôleur suivant
            superficieTextField.getScene().setRoot(root);

        } catch (SQLException | IOException e) {
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void retourliste(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeZones.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Liste des zones");
        stage.show();

    }
    public void setCoordinates(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        localisationTextField.setText("lat=" + lat + ";lng=" + lng);
    }
    @FXML
    void ouvrirMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterMapZone.fxml"));
            Parent root = loader.load();

            // Récupération du contrôleur Map
            AjouterMapZone ajoutermapzone = loader.getController();
            ajoutermapzone.setAjouterZoneController(this); // Important pour retour coords

            Stage stage = new Stage();
            stage.setTitle("Sélectionner une position sur la carte");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur d'ouverture de la carte : " + e.getMessage());
        }
    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            imagePathText.setText(selectedImageFile.getAbsolutePath());
        }
    }


}
