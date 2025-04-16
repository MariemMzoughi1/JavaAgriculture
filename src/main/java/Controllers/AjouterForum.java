package Controllers;

import Entites.Post;
import Services.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AjouterForum {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea contenuField;

    @FXML
    private TextField imageField;

    @FXML
    private ImageView imagePreview;

    private final PostService postService = new PostService();
    private File selectedImageFile;

    @FXML
    void parcourirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
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
        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();
        String imagePath = imageField.getText().trim();

        if (!isValidInput()) {
            return;
        }


        try {
            Post post = new Post(titre, contenu);
            if (!imagePath.isEmpty()) {
                post.setImage(imagePath);
            }

            postService.add(post);
            showAlert(Alert.AlertType.INFORMATION, "Post ajouté avec succès !");

            // Fermer la fenêtre actuelle
            ((javafx.stage.Stage)(((javafx.scene.Node) event.getSource()).getScene().getWindow())).close();

            // Ouvrir la nouvelle interface ListeForum.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeForum.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Liste des Forums");
            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du post : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void retour(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle
            ((Stage)((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();

            // Charger la vue de la liste des forums
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeForum.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Liste des Forums");
            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void clearFields() {
        titreField.clear();
        contenuField.clear();
        imageField.clear();
        imagePreview.setImage(null);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // Reset styles
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

        if (!imageField.getText().trim().isEmpty()) {
            String lowerPath = imageField.getText().toLowerCase();
            if (!(lowerPath.endsWith(".png") || lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg"))) {
                imageField.setStyle("-fx-border-color: red;");
                showAlert(Alert.AlertType.WARNING, "Le fichier sélectionné n'est pas une image valide.");
                isValid = false;
            } else {
                imageField.setStyle("");
            }
        }

        return isValid;
    }

}
