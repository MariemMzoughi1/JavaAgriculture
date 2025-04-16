package Controllers;

import Entites.Post;
import Services.PostService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeForum implements Initializable {

    @FXML
    private VBox cardContainer;

    @FXML
    private Button createForumButton; // Le bouton que tu viens d'ajouter

    // Méthode pour gérer le clic sur le bouton "Créer Forum"
    @FXML
    private void handleCreateForum(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) createForumButton.getScene().getWindow();
            currentStage.close();

            // Charger la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterForum.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Créer un Forum");

            // Charger et afficher la scène
            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de l'interface AjouterForum : " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PostService service = new PostService();
        List<Post> posts = service.find();

        // Trier par date décroissante (du plus récent au plus ancien)
        posts.sort((p1, p2) -> {
            if (p1.getDate() == null || p2.getDate() == null) return 0;
            return p2.getDate().compareTo(p1.getDate()); // plus récent en premier
        });

        for (Post p : posts) {
            VBox card = createCard(p);
            cardContainer.getChildren().add(card);
        }


    }


    private VBox createCard(Post post) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setMaxWidth(600);
        card.getStyleClass().add("card");

        Label titre = new Label(post.getTitre());
        titre.getStyleClass().add("titre-label");

        Label contenu = new Label(post.getContenu());
        contenu.setWrapText(true);
        contenu.getStyleClass().add("contenu-label");

        Label meta = new Label("Auteur ID : " + (post.getAuteurId() != null ? post.getAuteurId() : "N/A") +
                " | Date : " + (post.getDate() != null ? post.getDate().toString() : "N/A"));
        meta.getStyleClass().add("meta-label");

        Label stats = new Label("👍 " + post.getLikes() + "  👎 " + post.getDislikes());
        stats.getStyleClass().add("stats-label");

        ImageView imageView = new ImageView();
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            try {
                File file = new File(post.getImage());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                    imageView.setFitWidth(400);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                }
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + e.getMessage());
            }
        }

        // Boutons Voir plus et Supprimer
        HBox buttonBox = new HBox(10);
        Button voirPlusBtn = new Button("Voir plus");
        Button supprimerBtn = new Button("Supprimer");
        buttonBox.getChildren().addAll(voirPlusBtn, supprimerBtn);

        // Style facultatif pour les boutons
        voirPlusBtn.getStyleClass().add("button-voirplus");
        supprimerBtn.getStyleClass().add("button-supprimer");

        // Action bouton Voir plus
        voirPlusBtn.setOnAction(e -> {
            try {
                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) voirPlusBtn.getScene().getWindow();
                currentStage.close();

                // Charger la scène de détail
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsForum.fxml"));
                VBox root = loader.load();

                // Récupérer le contrôleur et lui passer le post
                DetailsForum controller = loader.getController();
                controller.setPost(post);

                // Afficher la nouvelle scène
                Stage detailStage = new Stage();
                detailStage.setTitle("Détails du forum");
                detailStage.setScene(new Scene(root));
                detailStage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Erreur lors de l'ouverture des détails du forum : " + ex.getMessage());
            }
        });



        // Action bouton Supprimer
        supprimerBtn.setOnAction(e -> {

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Voulez-vous vraiment supprimer ce post ?");
            alert.setContentText("Cette action est irréversible.");

            // Affiche l’alerte et attend la réponse de l’utilisateur
            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    PostService service = new PostService();
                    service.delete(post); // Supprimer de la BDD
                    cardContainer.getChildren().remove(card); // Supprimer de l'interface
                    System.out.println("Post supprimé !");
                } else {
                    System.out.println("Suppression annulée.");
                }
            });
        });


        if (imageView.getImage() != null) {
            card.getChildren().add(imageView);
        }

        card.getChildren().addAll(titre, contenu, meta, stats, buttonBox);

        return card;
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) createForumButton.getScene().getWindow();
            currentStage.close();

            // Charger la scène de machine-home.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-home.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Accueil");

            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du retour à l'accueil : " + e.getMessage());
        }
    }


}
