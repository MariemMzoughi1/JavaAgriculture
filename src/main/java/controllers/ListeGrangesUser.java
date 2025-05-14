package controllers;

import entities.Grange;
import services.GrangeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ListeGrangesUser {

    @FXML
    private FlowPane grangeContainer;

    @FXML
    private TextField searchField;

    private ObservableList<Grange> grangesList;
    private final GrangeService grangeService = new GrangeService();

    @FXML
    public void initialize() {
        grangesList = FXCollections.observableArrayList(grangeService.find());
        afficherGranges(grangesList);

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterGranges(newText);
        });
    }

    private void afficherGranges(ObservableList<Grange> granges) {
        grangeContainer.getChildren().clear();

        for (Grange grange : granges) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setPrefWidth(250);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8; " +
                    "-fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 4);");

            Label typeLabel = new Label("Type: " + grange.getType_grange());
            Label capaciteLabel = new Label("Capacité: " + grange.getCapacite());

            String unite = getUniteParType(grange.getType_grange());
            Label productiviteLabel = new Label("Productivité: " + grange.getProductivite() + " " + unite + " - " + getProductiviteNiveau(grange.getProductivite()));
            productiviteLabel.setStyle("-fx-text-fill: " + getProductiviteCouleur(grange.getProductivite()) + ";");

            Label zoneLabel = new Label("Zone ID: " + (grange.getZone() != null ? grange.getZone().getId() : "Non assignée"));

            card.getChildren().addAll(typeLabel, capaciteLabel, productiviteLabel, zoneLabel);
            grangeContainer.getChildren().add(card);
        }
    }

    private void filterGranges(String searchText) {
        ObservableList<Grange> filtered = FXCollections.observableArrayList();
        for (Grange g : grangesList) {
            if (g.getType_grange().toLowerCase().contains(searchText.toLowerCase())) {
                filtered.add(g);
            }
        }
        afficherGranges(filtered);
    }

    private String getProductiviteNiveau(float val) {
        if (val < 20) return "Faible";
        else if (val <= 50) return "Moyenne";
        else return "Élevée";
    }

    private String getProductiviteCouleur(float val) {
        if (val < 20) return "red";
        else if (val <= 50) return "orange";
        else return "green";
    }

    private String getUniteParType(String type) {
        if (type == null) return "";
        type = type.toLowerCase();
        switch (type) {
            case "poule":
            case "poules":
                return "œufs/jour";
            case "mouton":
            case "moutons":
                return "kg/mois";
            case "vache":
            case "vaches":
                return "litres/jour";
            default:
                return "unités/mois";
        }
    }

    @FXML
    public void trierParType() {
        FXCollections.sort(grangesList, (a, b) -> a.getType_grange().compareToIgnoreCase(b.getType_grange()));
        afficherGranges(grangesList);
    }

    @FXML
    public void trierParCapacite() {
        FXCollections.sort(grangesList, (a, b) -> Float.compare(a.getCapacite(), b.getCapacite()));
        afficherGranges(grangesList);
    }

    @FXML
    public void trierParProductivite() {
        FXCollections.sort(grangesList, (a, b) -> Float.compare(a.getProductivite(), b.getProductivite()));
        afficherGranges(grangesList);
    }

    @FXML
    private void ajouterGrange(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/projectjava/AjouterGrange.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ajouter Grange");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire d'ajout.");
        }
    }

    @FXML
    private void ouvrirChartProductivite(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/projectjava/Productivite_Chart.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Statistiques de Productivité");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du graphique.");
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
