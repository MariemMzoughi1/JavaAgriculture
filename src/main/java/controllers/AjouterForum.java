package controllers;

import entities.Post;
import entities.User;
import services.GeminiAPIService;
import services.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

public class AjouterForum {

    @FXML private TextField titreField;
    @FXML private TextArea contenuField;
    @FXML private TextField imageField;
    @FXML private ImageView imagePreview;

    private final PostService postService = new PostService();
    private File selectedImageFile;

    // ⚠️ Chemin absolu vers le dossier public de Symfony (adapter selon ta machine)
    private static final String SYMFONY_UPLOAD_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/posts/";

    @FXML
    void parcourirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            imageField.setText(file.getAbsolutePath());
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    void ajouterPost(ActionEvent event) {
        if (!isValidInput()) return;

        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();

        try {
            if (GeminiAPIService.contientBadWords(titre) || GeminiAPIService.contientBadWords(contenu)) {
                showAlert(Alert.AlertType.ERROR, "Titre ou contenu contient des propos inappropriés.");
                return;
            }

            if (!GeminiAPIService.estLieAAgriculture(titre) || !GeminiAPIService.estLieAAgriculture(contenu)) {
                showAlert(Alert.AlertType.ERROR, "Titre ou contenu non lié à l'agriculture.");
                return;
            }

            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Aucun utilisateur connecté !");
                return;
            }

            Post post = new Post(titre, contenu);
            post.setDate(new Date());
            post.setLikes(0);
            post.setDislikes(0);
            post.setAuteurId(currentUser.getId());

            if (selectedImageFile != null) {
                String imageFileName = selectedImageFile.getName();
                File destination = new File(SYMFONY_UPLOAD_DIR + imageFileName);

                // Copier l'image dans Symfony
                Files.copy(selectedImageFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Enregistrer uniquement le nom de l'image
                post.setImage(imageFileName);
            }

            postService.add(post);
            showAlert(Alert.AlertType.INFORMATION, "Post ajouté avec succès !");

            // Redirection vers la liste des posts
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeForum.fxml"));
            Stage newStage = new Stage();
            newStage.setTitle("Liste des Forums");
            newStage.setScene(new Scene(loader.load()));
            newStage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'enregistrement de l'image : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du post : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void retour(ActionEvent event) {
        try {
            ((Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeForum.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Liste des Forums");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        titreField.setStyle("");
        contenuField.setStyle("");

        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();

        if (titre.isEmpty() || titre.length() < 5) {
            titreField.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.WARNING, "Le titre doit contenir au moins 5 caractères.");
            isValid = false;
        }

        if (contenu.isEmpty() || contenu.length() < 10) {
            contenuField.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.WARNING, "Le contenu doit contenir au moins 10 caractères.");
            isValid = false;
        }

        return isValid;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
