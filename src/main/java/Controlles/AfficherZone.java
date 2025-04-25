package Controlles;

import java.net.URL;
import java.util.ResourceBundle;

import Entites.Zone;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

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
    private ImageView imageView; // Déclaration de l'ImageView pour l'affichage de l'image

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
    }
}
