package Controlles;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Entites.Grange;
import Entites.Zone;
import Services.GrangeService;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import Services.ZoneService;



public class AfficherZone {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField localisation;

    @FXML
    private TextField nomdezone;

    @FXML
    private TextField superficie;

    @FXML
    private WebView mapView;

    @FXML
    private ImageView imageView;

    @FXML
    private VBox grangesList;  // Déclaration du VBox

    @FXML
    private Button btnProposerCulture;

    public void setSuperficie(float superficie) {
        this.superficie.setText(Float.toString(superficie));
    }

    public void setNomdezone(String nomdezone) {
        this.nomdezone.setText(nomdezone);
    }

    public void setLocalisation(String localisation) {
        this.localisation.setText(localisation);
    }

    // Méthode pour afficher l'image
    public void setImage(String image) {
        if (image != null && !image.isEmpty()) {
            Image img = new Image("file:" + image); // Charger l'image depuis le chemin
            imageView.setImage(img);  // Afficher l'image dans l'ImageView
        }
    }

    @FXML
    void initialize() {
        // Initialisation, si nécessaire
    }

    @FXML
    private void retourListeZones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeZones.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Liste des zones");
        stage.show();
    }

    // Méthode pour définir la zone et afficher ses informations
    public void setZone(Zone zone) {
        setNomdezone(zone.getNom_zone());
        setSuperficie(zone.getSuperficie_zone());
        setLocalisation(zone.getLocalisation_zone());
        setImage(zone.getImage()); // Utiliser le champ image
        afficherGranges(zone.getId());
        afficherZoneAvecCarte(zone.getId());
    }
    public void afficherZoneAvecCarte(int zoneId) {
        // Récupérer la zone depuis la base de données
        ZoneService zoneService = new ZoneService();  // Création de l'instance
        Zone zone = zoneService.findById(zoneId);    // Appel de la méthode sur l'instance


        if (zone != null) {
            // Récupérer la localisation de la zone
            String localisation = zone.getLocalisation_zone();

            // Appeler la méthode afficherCarte pour afficher la carte avec les coordonnées
            afficherCarte(localisation);
        } else {
            System.out.println("Zone non trouvée.");
        }
    }
    private void afficherCarte(String localisation) {
        if (localisation == null || localisation.isEmpty() || !localisation.contains(",")) {
            System.out.println("Localisation invalide.");
            return;
        }

        // Séparer la latitude et la longitude
        String[] parts = localisation.split(",");
        String latitude = parts[0].trim();
        String longitude = parts[1].trim();

        // Charger le fichier HTML à partir des ressources
        WebEngine webEngine = mapView.getEngine();
        URL mapFileUrl = getClass().getResource("/mapbox.html");
        webEngine.load(mapFileUrl.toExternalForm());

        // Attendre que la page HTML soit chargée avant d'envoyer les coordonnées via JavaScript
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Appel JS pour envoyer les coordonnées à la carte
                String script = String.format("if (window.javaConnector) { window.javaConnector.sendCoordinates(%s, %s); }", longitude, latitude);
                webEngine.executeScript(script);
            }
        });
    }

    // Méthode pour afficher la liste des granges associées à la zone
    private void afficherGranges(int zoneId) {
        GrangeService grangeService = new GrangeService();
        List<Grange> granges = grangeService.find();  // Récupérer toutes les granges depuis la base de données

        // Filtrer les granges en fonction de la zoneId
        List<Grange> grangesPourZone = new ArrayList<>();
        for (Grange grange : granges) {
            if (grange.getZone().getId() == zoneId) {
                grangesPourZone.add(grange);
            }
        }

        // Vider la liste précédente pour ne pas afficher d'éléments du passé
        grangesList.getChildren().clear();

        // Ajouter les granges dans la VBox
        if (!grangesPourZone.isEmpty()) {
            for (Grange grange : grangesPourZone) {
                // Créer un Label avec les détails de la grange
                Label grangeLabel = new Label("Grange: " + grange.getType_grange() + " - Capacité: " + grange.getCapacite() + " - Productivité: " + grange.getProductivite());
                grangesList.getChildren().add(grangeLabel);  // Ajouter chaque grange à la VBox
            }
        } else {
            Label noGrangesLabel = new Label("Aucune grange associée à cette zone.");
            grangesList.getChildren().add(noGrangesLabel);
        }
    }




    @FXML
    private void proposerCulture(ActionEvent event) {
        String superficieText = superficie.getText(); // Récupérer le texte du TextField

        if (superficieText == null || superficieText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une superficie valide.");
            alert.showAndWait();
            return;
        }

        double superficieZone;
        try {
            superficieZone = Double.parseDouble(superficieText); // Convertir le texte en double
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de format");
            alert.setHeaderText(null);
            alert.setContentText("La superficie doit être un nombre !");
            alert.showAndWait();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Cultures proposées");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("Cultures recommandées");
        titre.setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        VBox culturesBox = new VBox(10);
        culturesBox.setAlignment(Pos.CENTER);

        if (superficieZone < 500) {
            culturesBox.getChildren().addAll(
                    new Label("- Tomates"),
                    new Label("- Fraises"),
                    new Label("- Salades")
            );
        } else if (superficieZone >= 500 && superficieZone < 2000) {
            culturesBox.getChildren().addAll(
                    new Label("- Orangers"),
                    new Label("- Pêchers")
            );
        } else {
            culturesBox.getChildren().addAll(
                    new Label("- Oliviers"),
                    new Label("- Vignes"),
                    new Label("- Amandiers")
            );
        }

        Button fermerButton = new Button("Fermer");
        fermerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        fermerButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(titre, culturesBox, fermerButton);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }


}
