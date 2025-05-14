package controllers;

import entities.Commande;
import services.CommandeService;
import services.EmailService;
import services.FactureService;
import services.PaymentService;
import com.lowagie.text.DocumentException;
import com.stripe.model.checkout.Session;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
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
    private Commande commande;

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
                paymentStage.close();

                try {
                    // ✅ Créer la commande
                    Commande commande = new Commande("En attente", LocalDateTime.now(), totalCommande);

                    // ✅ Ajouter la commande
                    new CommandeService().ajouterCommande(commande);

                    // ✅ Envoyer email de confirmation
                    EmailService.sendConfirmationEmail(emailField.getText());

                    // ✅ Générer facture PDF
                    FactureService.generateInvoice(commande, emailField.getText());

                    // ✅ Alerte de succès
                    showAlert(Alert.AlertType.INFORMATION, "Paiement réussi",
                            "Votre paiement a été effectué avec succès.\nUn e-mail de confirmation vous a été envoyé.\nLa facture a été générée sur votre bureau.");

                    // ✅ Revenir à la page commande.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) formulaireView.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Liste des Produits");
                    stage.show();

                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la génération de la facture ou chargement page.");
                }

            } else if (newValue.contains("cancel")) {
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
