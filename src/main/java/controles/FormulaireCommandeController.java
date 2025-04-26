package controles;

import Entites.Commande;
import Services.CommandeService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.Arrays;

public class FormulaireCommandeController {

    @FXML private VBox formulaireView;  // Ajout de la référence VBox
    @FXML private TextField nomField;
    @FXML private TextField telephoneField;
    @FXML private ComboBox<String> gouvernoratComboBox;
    @FXML private TextField adresseDetailField;
    @FXML private TextField emailField;

    private double totalCommande;

    public void setTotalCommande(double total) {
        this.totalCommande = total;
    }

    @FXML
    private void initialize() {
        // Remplir la ComboBox avec les gouvernorats tunisiens
        gouvernoratComboBox.getItems().addAll(
                Arrays.asList(
                        "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan",
                        "Bizerte", "Béja", "Jendouba", "Kef", "Siliana", "Sousse", "Monastir",
                        "Mahdia", "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid", "Gabès",
                        "Médenine", "Tataouine", "Gafsa", "Tozeur", "Kébili"
                )
        );

        // Initialiser le champ téléphone avec +216
        telephoneField.setText("+216");

        // Fade-in effect
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), formulaireView);
        fadeTransition.setFromValue(0);  // Commence invisible
        fadeTransition.setToValue(1);    // Devient visible
        fadeTransition.play();
    }

    @FXML
    private void onConfirmer() {
        if (nomField.getText().isEmpty() || telephoneField.getText().isEmpty() ||
                gouvernoratComboBox.getValue() == null || adresseDetailField.getText().isEmpty() ||
                emailField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Merci de remplir tous les champs !");
            return;
        }

        // Vérifier que le nom contient seulement des lettres
        if (!nomField.getText().matches("[a-zA-Z\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom doit contenir uniquement des lettres.");
            return;
        }

        // Vérifier téléphone : doit commencer par +216 suivi de 8 chiffres
        if (!telephoneField.getText().matches("\\+216\\d{8}")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro doit contenir 8 chiffres après +216.");
            return;
        }

        // Vérifier que l'email est valide
        if (!emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Adresse e-mail invalide.");
            return;
        }

        // Créer la commande
        Commande commande = new Commande("En attente", LocalDateTime.now(), totalCommande);
        new CommandeService().ajouterCommande(commande);

        showAlert(Alert.AlertType.INFORMATION, "Commande réussie", "Votre commande a été enregistrée !");

        // Fermer la fenêtre
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
