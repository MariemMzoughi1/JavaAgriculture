package controles;

import Entites.Commande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CommandeDetailsController {

    @FXML
    private Label labelEtat;

    @FXML
    private Label labelTotal;

    public void setCommande(Commande commande) {
        // Affichage de l'état et du total
        labelEtat.setText("État : " + commande.getEtat());
        labelTotal.setText("Total : " + commande.getTotal() + " DT");
    }

    @FXML
    private void fermer(ActionEvent event) {
        // Récupère la fenêtre (Stage) actuelle
        Stage stage = (Stage) labelEtat.getScene().getWindow();

        // Ferme la fenêtre
        stage.close();
    }
}
