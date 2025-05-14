package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

import entities.Zone;
import services.ZoneService;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private String imagePath = "";

    private double latitude;
    private double longitude;

    @FXML
    public void initialize() {
        // Rien à initialiser
    }

    @FXML
    void AjouterZone(ActionEvent event) {
        String superficieStr = superficieTextField.getText();
        String nomZone = nomzoneTextField.getText();
        String localisation = localisationTextField.getText();
        String image = imagePathText.getText();

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
        ZoneService zoneservice = new ZoneService();

        try {
            zoneservice.add(zone);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Zone ajoutée avec succès !");
            alert.showAndWait();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeZonesUser.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Zones");
            stage.show();

        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
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
            String imageName = selectedImageFile.getName();

            // Dossier de destination Symfony
            String symfonyTargetDir = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/zones/";
            File targetDir = new File(symfonyTargetDir);
            if (!targetDir.exists()) targetDir.mkdirs();

            File targetFile = new File(symfonyTargetDir + imageName);

            try {
                Files.copy(selectedImageFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = imageName;
                imagePathText.setText(imagePath);
            } catch (IOException e) {
                showAlert("Erreur lors de la copie de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void retourliste(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeZonesUser.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Zones");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur de retour à la liste : " + e.getMessage());
        }
    }

    public void setCoordinates(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        localisationTextField.setText("lat=" + lat + ";lng=" + lng);
    }

    @FXML
    void ouvrirMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AjouterMapZone.fxml"));
            Parent root = loader.load();

            AjouterMapZone ajoutermapzone = loader.getController();
            ajoutermapzone.setAjouterZoneController(this);

            Stage stage = new Stage();
            stage.setTitle("Sélectionner une position sur la carte");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur d'ouverture de la carte : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
