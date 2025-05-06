package controllers;

import entities.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.PostService;

public class EditPostController {

    @FXML private TextField titreField;
    @FXML private TextArea contenuField;

    private final PostService postService = new PostService();
    private Post currentPost;

    public void setPost(Post post) {
        this.currentPost = post;
        titreField.setText(post.getTitre());
        contenuField.setText(post.getContenu());
    }

    @FXML
    private void handleSave() {
        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();

        if (titre.isEmpty() || contenu.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
            return;
        }

        currentPost.setTitre(titre);
        currentPost.setContenu(contenu);

        boolean updated = postService.maj(currentPost);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Post mis à jour avec succès !");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour du post.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
