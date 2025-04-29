package Controlles;

import Entites.Grange;
import Services.GrangeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ListeGrangesUser {

    @FXML private FlowPane grangeContainer;
    @FXML private TextField searchField;

    private ObservableList<Grange> grangesList;
    private final GrangeService grangeService = new GrangeService();

    @FXML
    public void initialize() {
        grangesList = FXCollections.observableArrayList(grangeService.find());
        afficherGranges(grangesList);

        // 🔍 Écouteur de recherche
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterGranges(newText);
        });
    }

    private void afficherGranges(ObservableList<Grange> granges) {
        grangeContainer.getChildren().clear();

        for (Grange grange : granges) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setPrefWidth(200);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 4);");

            Label typeLabel = new Label("Type: " + grange.getType_grange());
            Label capaciteLabel = new Label("Capacité: " + grange.getCapacite());
            Label productiviteLabel = new Label("Productivité: " + grange.getProductivite() + " - " + getProductiviteNiveau(grange.getProductivite()));
            Label zoneLabel = new Label("Zone ID: " + (grange.getZone() != null ? grange.getZone().getId() : "Non assignée"));

            productiviteLabel.setStyle("-fx-text-fill: " + getProductiviteCouleur(grange.getProductivite()) + ";");

            card.getChildren().addAll(typeLabel, capaciteLabel, productiviteLabel, zoneLabel);
            grangeContainer.getChildren().add(card);
        }
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

    private void filterGranges(String searchText) {
        ObservableList<Grange> filtered = FXCollections.observableArrayList();
        for (Grange g : grangesList) {
            if (g.getType_grange().toLowerCase().contains(searchText.toLowerCase())) {
                filtered.add(g);
            }
        }
        afficherGranges(filtered);
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
    void retourAccueil(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Acceuil.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void ajouterGrange(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterGrange.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Grange");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void ouvrirChartProductivite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/productivite_chart.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Graphique de Productivité");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
