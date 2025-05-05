package Controllers.AdminForum;

import Controllers.forum.DetailsForum;
import Entites.Post;
import Services.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

public class AdminModifierPost {

    @FXML private TextField titreField;
    @FXML private TextArea contenuArea;
    @FXML private ImageView imageView;
    @FXML private Button boutonImage;

    private Post post;
    private String selectedImagePath = null;

    public void setPost(Post post) {
        this.post = post;

        titreField.setText(post.getTitre());
        contenuArea.setText(post.getContenu());

        if (post.getImage() != null && !post.getImage().isEmpty()) {
            File file = new File(post.getImage());
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

        File selectedFile = fileChooser.showOpenDialog(titreField.getScene().getWindow());
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            imageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!isValidInput()) {
            return;
        }

        post.setTitre(titreField.getText());
        post.setContenu(contenuArea.getText());
        if (selectedImagePath != null) {
            post.setImage(selectedImagePath);
        }

        PostService postService = new PostService();
        postService.updatePost(post);

        // Retour à la page de détails du forum avec le post modifié
        try {
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDetailsForum.fxml"));
            VBox root = loader.load();

            AdminDetailsForum controller = loader.getController();
            controller.setPost(post); // Important : passer le post mis à jour

            Stage newStage = new Stage();
            newStage.setTitle("Détails du Forum");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAnnuler() {
        try {
            // Fermer la fenêtre actuelle
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.close();

            // Charger le fichier FXML de la page de détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDetailsForum.fxml"));
            VBox root = loader.load();

            // Obtenir le contrôleur et lui passer le post
            AdminDetailsForum controller = loader.getController();
            controller.setPost(post); // restaurer les infos du post

            // Créer et afficher la nouvelle scène
            Stage newStage = new Stage();
            newStage.setTitle("Détails du Forum");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isValidInput() {
        boolean isValid = true;

        // Réinitialiser les styles
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

        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            String lowerPath = selectedImagePath.toLowerCase();
            if (!(lowerPath.endsWith(".png") || lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg"))) {
                showAlert(Alert.AlertType.WARNING, "Le fichier sélectionné n'est pas une image valide.");
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
