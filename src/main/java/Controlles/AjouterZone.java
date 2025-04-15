package Controlles;

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
    void AjouterZone(ActionEvent event) {
        String superficieStr = superficieTextField.getText();
        String nomZone = nomzoneTextField.getText();
        String localisation = localisationTextField.getText();


        if (superficieStr.isEmpty() || nomZone.isEmpty() || localisation.isEmpty()) {
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


        if (localisation.length() < 3) {
            showAlert("La localisation doit contenir au moins 3 caractères.");
            return;
        }


        Zone zone = new Zone(superficie, nomZone, localisation);
        ZoneService zoneservice = new ZoneService();

        try {
            zoneservice.add(zone);

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



    @FXML
    void initialize() {
    }




}