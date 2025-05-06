package controllers;

import entities.Post;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.MailService;
import services.PostService;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminPostController implements Initializable {

    @FXML private TableView<Post> postTable;
    @FXML private TableColumn<Post, Integer> idColumn;
    @FXML private TableColumn<Post, String> titreColumn;
    @FXML private TableColumn<Post, String> auteurColumn;
    @FXML private TableColumn<Post, Void> actionsColumn;

    private final PostService postService = new PostService();
    private final UserService userService = new UserService();
    private final ObservableList<Post> postList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurColumn.setCellValueFactory(cellData -> {
            Integer auteurId = cellData.getValue().getAuteurId();
            String email = (auteurId != null) ? userService.getEmailById(auteurId) : "Inconnu";
            return new javafx.beans.property.SimpleStringProperty(email);
        });



        loadPosts();
        addActionButtons();
    }

    private void loadPosts() {
        postList.setAll(postService.find());
        postTable.setItems(postList);
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox box = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Post selectedPost = getTableView().getItems().get(getIndex());
                    openEditDialog(selectedPost);
                });

                deleteBtn.setOnAction(event -> {
                    Post selectedPost = getTableView().getItems().get(getIndex());
                    confirmAndDeletePost(selectedPost);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void openEditDialog(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/EditPost.fxml"));
            VBox root = loader.load();

            EditPostController controller = loader.getController();
            controller.setPost(post); // méthode à créer dans EditPostController

            Stage stage = new Stage();
            stage.setTitle("Modifier le Post");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadPosts(); // refresh après modification
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void confirmAndDeletePost(Post post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce post ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Récupération de l'auteur via postService
                User auteur = postService.getAuteurByPostId(post.getId());

                // Suppression du post
                postService.delete(post);

                // Envoi d'e-mail de notification
                if (auteur != null && auteur.getEmail() != null) {
                    MailService mailService = new MailService();
                    String subject = "Suppression de votre post";
                    String content = "Bonjour " + auteur.getEmail() + ",\n\n"
                            + "Votre post intitulé \"" + post.getTitre() + "\" a été supprimé par l'administrateur.\n\n"
                            + "Cordialement,\nL'équipe de modération.";
                    mailService.envoyerMail(auteur.getEmail(), subject, content);
                }

                // Actualiser l'affichage
                loadPosts();
            }
        });
    }

}
