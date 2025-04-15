package Controlles;

import Entites.Grange;
import Services.GrangeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AjouterGrange {

    @FXML
    private TextField typeGrangeField;

    @FXML
    private TextField capaciteField;

    @FXML
    private Label messageErreur;

    private final GrangeService grangeService = new GrangeService();

    @FXML
    void ajouterGrange(ActionEvent event) {
        String type = typeGrangeField.getText().trim();
        String capaciteText = capaciteField.getText().trim();

        if (type.isEmpty() || capaciteText.isEmpty()) {
            messageErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            float capacite = Float.parseFloat(capaciteText);
            Grange grange = new Grange(type, capacite);
            grangeService.add(grange);
            messageErreur.setText("Grange ajoutée avec succès !");
            messageErreur.setStyle("-fx-text-fill: green;");

            // Réinitialiser les champs
            typeGrangeField.clear();
            capaciteField.clear();
        } catch (NumberFormatException e) {
            messageErreur.setText("Capacité doit être un nombre valide.");
        } catch (Exception e) {
            messageErreur.setText("Erreur lors de l'ajout de la grange.");
            e.printStackTrace();
        }
    }
}
