package controllers;

import entities.Zone;
import services.ZoneService;
import utils.ExportCSV;
import utils.ExportPDF;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListeZonesUser implements Initializable {

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button btnRetour;

    @FXML
    private FlowPane zoneContainer;

    @FXML
    private TextField searchField;

    private final ZoneService zoneService = new ZoneService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherToutesLesZones();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> rechercherZones());
    }

    private VBox createZoneCard(Zone zone) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c8e6c9; -fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);

        Text nom = new Text("🌱 " + zone.getNom_zone());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Text superficie = new Text("🌍 " + zone.getSuperficie_zone() + " ha");
        Text localisation = new Text("📍 " + zone.getLocalisation_zone());

        Button consulterBtn = new Button("Consulter");
        consulterBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
        consulterBtn.setOnAction(e -> ouvrirDetailsZone(zone));

        card.getChildren().addAll(nom, superficie, localisation, consulterBtn);
        return card;
    }

    private void afficherToutesLesZones() {
        List<Zone> zones = zoneService.find();
        zoneContainer.getChildren().clear();
        for (Zone zone : zones) {
            zoneContainer.getChildren().add(createZoneCard(zone));
        }
    }

    @FXML
    private void ajouterZone(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AjouterZone.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Zone");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la vue AjouterZone.");
        }
    }

    private void ouvrirDetailsZone(Zone zone) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AfficherZone.fxml"));
            Parent root = loader.load();

            // Injection de l'objet zone
            AfficherZone controller = loader.getController();
            controller.setZone(zone);

            Stage stage = new Stage();
            stage.setTitle("Détails de la Zone");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les détails de la zone.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void rechercherZones() {
        String keyword = searchField.getText().toLowerCase();
        List<Zone> zones = zoneService.find().stream()
                .filter(zone -> zone.getNom_zone().toLowerCase().contains(keyword)
                        || zone.getLocalisation_zone().toLowerCase().contains(keyword)
                        || String.valueOf(zone.getSuperficie_zone()).contains(keyword))
                .collect(Collectors.toList());

        zoneContainer.getChildren().clear();
        for (Zone zone : zones) {
            zoneContainer.getChildren().add(createZoneCard(zone));
        }
    }

    @FXML
    private void trierParNom(ActionEvent event) {
        List<Zone> zonesTriees = zoneService.find().stream()
                .sorted(Comparator.comparing(Zone::getNom_zone))
                .collect(Collectors.toList());

        zoneContainer.getChildren().clear();
        zonesTriees.forEach(zone -> zoneContainer.getChildren().add(createZoneCard(zone)));
    }

    @FXML
    private void trierParSuperficie(ActionEvent event) {
        List<Zone> zonesTriees = zoneService.find().stream()
                .sorted(Comparator.comparingDouble(Zone::getSuperficie_zone))
                .collect(Collectors.toList());

        zoneContainer.getChildren().clear();
        zonesTriees.forEach(zone -> zoneContainer.getChildren().add(createZoneCard(zone)));
    }

    @FXML
    private void exporterCSV(ActionEvent event) {
        List<Zone> zones = zoneService.find();
        ExportCSV.exportZonesToCSV(zones);
    }

    @FXML
    private void exporterPDF(ActionEvent event) {
        List<Zone> zones = zoneService.find();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.setInitialFileName("zones.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            try {
                ExportPDF.exporterZonesEnPDF(zones, selectedFile.getAbsolutePath());
                showAlert("Succès", "Exportation PDF réussie !");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'exportation : " + e.getMessage());
            }
        }
    }
}
