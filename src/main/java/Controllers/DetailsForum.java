package Controllers;

import Entites.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Entites.Commentaire;
import Services.CommentaireService;

import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class DetailsForum {

    @FXML private Label titreLabel;
    @FXML private Label auteurDateLabel;
    @FXML private TextArea contenuArea;
    @FXML private Label likesDislikesLabel;
    @FXML private ImageView imageView;
    @FXML private VBox commentairesBox;
    @FXML private TextField champCommentaire;

    private Post post;

    private void afficherCommentaires() {
        CommentaireService service = new CommentaireService();
        List<Commentaire> commentaires = service.getCommentairesParPostId(post.getId());

        commentairesBox.getChildren().clear();

        for (Commentaire c : commentaires) {
            VBox commentaireCard = new VBox(5);
            commentaireCard.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 10; -fx-background-radius: 8;");

            Label auteurLabel = new Label("Auteur ID " + c.getAuteurId());
            TextField contenuField = new TextField(c.getContenu());
            contenuField.setEditable(false); // non éditable au début
            contenuField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

            HBox boutons = new HBox(10);
            Button modifierBtn = new Button("Modifier");
            Button enregistrerBtn = new Button("Enregistrer");
            Button supprimerBtn = new Button("Supprimer");

            enregistrerBtn.setVisible(false); // caché par défaut

            modifierBtn.setOnAction(e -> {
                contenuField.setEditable(true);
                contenuField.setStyle(""); // enlever le style transparent
                enregistrerBtn.setVisible(true);
                modifierBtn.setVisible(false);
            });

            enregistrerBtn.setOnAction(e -> {
                String nouveauTexte = contenuField.getText().trim();
                if (!nouveauTexte.isEmpty()) {
                    c.setContenu(nouveauTexte);
                    new CommentaireService().modifierCommentaire(c);
                    afficherCommentaires(); // rafraîchir
                }
            });

            supprimerBtn.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText("Voulez-vous vraiment supprimer ce commentaire ?");
                confirmation.setContentText("Cette action est irréversible.");

                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    service.supprimerCommentaire(c.getId());
                    afficherCommentaires(); // rafraîchir
                }
            });

            // Tu peux aussi ajouter ici un check si l'utilisateur connecté est bien l'auteur
            boutons.getChildren().addAll(modifierBtn, enregistrerBtn, supprimerBtn);

            commentaireCard.getChildren().addAll(auteurLabel, contenuField, boutons);
            commentairesBox.getChildren().add(commentaireCard);
        }

    }


    @FXML
    private void ajouterCommentaire() {
        String contenu = champCommentaire.getText().trim();
        if (contenu.isEmpty()) {
            return;
        }

        Commentaire commentaire = new Commentaire(post.getId(), contenu, 1, new Date()); // remplacer 1 par l'ID de l'utilisateur connecté
        CommentaireService service = new CommentaireService();
        service.ajouterCommentaire(commentaire);

        champCommentaire.clear();
        afficherCommentaires();
    }

    private void modifierCommentaire(Commentaire c) {
        TextInputDialog dialog = new TextInputDialog(c.getContenu());
        dialog.setTitle("Modifier le commentaire");
        dialog.setHeaderText(null);
        dialog.setContentText("Nouveau contenu :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContenu -> {
            if (!newContenu.trim().isEmpty()) {
                c.setContenu(newContenu.trim());
                new CommentaireService().modifierCommentaire(c);
                afficherCommentaires();
            }
        });
    }


    public void setPost(Post post) {
        this.post = post;

        // Remplir l'interface
        titreLabel.setText(post.getTitre());
        auteurDateLabel.setText("Auteur ID : " + post.getAuteurId() + " | Date : " + post.getDate());
        contenuArea.setText(post.getContenu());
        likesDislikesLabel.setText("👍 " + post.getLikes() + "    👎 " + post.getDislikes());

        if (post.getImage() != null && !post.getImage().isEmpty()) {
            File file = new File(post.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
        afficherCommentaires();
    }

    @FXML
    private void handleRetour() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) titreLabel.getScene().getWindow();
        stage.close();

        // Réouvrir la fenêtre principale (liste des forums)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeForum.fxml"));
            Stage listestage = new Stage();
            listestage.setTitle("Liste des Forums");
            listestage.setScene(new Scene(loader.load()));
            listestage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPost.fxml"));
            VBox root = loader.load();

            ModifierPost controller = loader.getController();
            controller.setPost(post); // Passer le post à modifier

            Stage stage = new Stage();
            stage.setTitle("Modifier le Post");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) titreLabel.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
