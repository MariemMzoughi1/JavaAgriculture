package controllers;

import entities.Post;
import javafx.scene.control.Alert;
import services.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModifierPost {

    @FXML private TextField titreField;
    @FXML private TextArea contenuArea;
    @FXML private ImageView imageView;
    @FXML private Button boutonImage;

    private Post post;
    private File selectedImageFile;

    // 🔁 Adapter le chemin vers le dossier uploads de Symfony sur ta machine :
    private static final String SYMFONY_UPLOAD_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/posts/";

    public void setPost(Post post) {
        this.post = post;

        titreField.setText(post.getTitre());
        contenuArea.setText(post.getContenu());

        // Charger l'image depuis Symfony si elle existe
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            File file = new File(SYMFONY_UPLOAD_DIR + post.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );

        selectedImageFile = fileChooser.showOpenDialog(titreField.getScene().getWindow());
        if (selectedImageFile != null) {
            imageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!isValidInput()) return;

        post.setTitre(titreField.getText());
        post.setContenu(contenuArea.getText());

        if (selectedImageFile != null) {
            try {
                String fileName = selectedImageFile.getName();
                File destination = new File(SYMFONY_UPLOAD_DIR + fileName);

                // Copier l'image dans le dossier Symfony
                Files.copy(selectedImageFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Enregistrer uniquement le nom
                post.setImage(fileName);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la copie de l'image : " + e.getMessage());
                return;
            }
        }

        // Mise à jour dans la base
        PostService postService = new PostService();
        postService.updatePost(post);

        // Retour à la page de détails
        try {
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/DetailsForum.fxml"));
            VBox root = loader.load();

            DetailsForum controller = loader.getController();
            controller.setPost(post);

            Stage newStage = new Stage();
            newStage.setTitle("Détails du Forum");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour aux détails : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        try {
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/DetailsForum.fxml"));
            VBox root = loader.load();

            DetailsForum controller = loader.getController();
            controller.setPost(post);

            Stage newStage = new Stage();
            newStage.setTitle("Détails du Forum");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'annulation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;

        titreField.setStyle("");
        contenuArea.setStyle("");

        String titre = titreField.getText().trim();
        String contenu = contenuArea.getText().trim();

        if (titre.isEmpty() || titre.length() < 5) {
            titreField.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.WARNING, "Le titre doit contenir au moins 5 caractères.");
            isValid = false;
        }

        if (contenu.isEmpty() || contenu.length() < 10) {
            contenuArea.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.WARNING, "Le contenu doit contenir au moins 10 caractères.");
            isValid = false;
        }

        if (selectedImageFile != null) {
            String name = selectedImageFile.getName().toLowerCase();
            if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))) {
                showAlert(Alert.AlertType.WARNING, "Fichier image non valide.");
                isValid = false;
            }
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
