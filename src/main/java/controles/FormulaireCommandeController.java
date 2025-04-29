package controles;

import Entites.Commande;
import Services.CommandeService;
import Services.EmailService;
import Services.PaymentService;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.awt.Desktop;
import java.net.URI;
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

        // Gestion de l'événement de saisie pour le téléphone
        telephoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Empêcher de modifier le préfixe +216 et d’écrire autre chose que des chiffres
            if (!newValue.startsWith("+216")) {
                telephoneField.setText("+216");
                return;
            }

            // Récupérer juste la partie après +216
            String numbersOnly = newValue.substring(4).replaceAll("[^\\d]", "");

            // Limiter à 8 chiffres
            if (numbersOnly.length() > 8) {
                numbersOnly = numbersOnly.substring(0, 8);
            }

            // Remettre la valeur formatée
            telephoneField.setText("+216" + numbersOnly);
        });
    }

    @FXML
    private void onConfirmer() {
        // Vérification des champs
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

        // Vérifier que le numéro de téléphone commence par +216 et contient 8 chiffres après
        if (!telephoneField.getText().matches("\\+216\\d{8}")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro doit commencer par +216 suivi de 8 chiffres.");
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

        try {
            // Création de la session de paiement
            PaymentService paymentService = new PaymentService();
            Session session = paymentService.createCheckoutSession(totalCommande);

            String checkoutUrl = session.getUrl(); // ← L'URL à ouvrir pour payer

            openPaymentPage(checkoutUrl);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du paiement.");
            e.printStackTrace();
        }
    }

    private void openPaymentPage(String url) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(url);

        Stage paymentStage = new Stage();
        paymentStage.setTitle("Paiement sécurisé");
        paymentStage.setScene(new Scene(webView, 800, 600));
        paymentStage.show();

        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Navigated to: " + newValue);

            if (newValue.contains("success")) {
                // Paiement réussi

                // Fermer la fenêtre de paiement
                paymentStage.close();

                // Envoyer l'email de confirmation
                EmailService.sendConfirmationEmail(emailField.getText());

                // Afficher une alerte de succès
                showAlert(Alert.AlertType.INFORMATION, "Paiement réussi", "Votre paiement a été effectué avec succès. Un e-mail de confirmation vous a été envoyé.");

                // 🔥 Changer la scène vers la page ListeProduits.fxml
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) formulaireView.getScene().getWindow(); // récupérer la fenêtre actuelle
                    stage.setScene(new Scene(root));
                    stage.setTitle("Liste des Produits");
                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des produits.");
                }
            }
            else if (newValue.contains("cancel")) {
                // Paiement annulé
                paymentStage.close();
                showAlert(Alert.AlertType.ERROR, "Paiement annulé", "Le paiement a été annulé.");
            }
        });
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
