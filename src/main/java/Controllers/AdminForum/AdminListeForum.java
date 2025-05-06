package Controllers.AdminForum;

import Controllers.forum.DetailsForum;
import Entites.Post;
import Entites.User;
import Services.MailService;
import Services.PostService;
import Services.Session;
import Services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminListeForum implements Initializable {

    @FXML
    private VBox cardContainer;

    @FXML
    private Button createForumButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button sortByDateButton;

    @FXML
    private Button sortByLikesButton;

    @FXML
    private Button sortByDislikesButton;

    private UserService userService = new UserService();
    private PostService service = new PostService();
    private List<Post> allPosts;
    private List<Post> displayedPosts;
    private boolean isDateAscending = false;
    private boolean isLikesAscending = false;
    private boolean isDislikesAscending = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPosts();
        setupResponsiveBehavior();
    }

    private void setupResponsiveBehavior() {
        cardContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            refreshPosts();
        });
    }

    private void loadPosts() {
        allPosts = service.find();
        if (allPosts == null) {
            allPosts = new ArrayList<>();
        }

        // Tri par date décroissante (du plus récent au plus ancien)
        allPosts.sort((p1, p2) -> {
            if (p1.getDate() == null || p2.getDate() == null) return 0;
            return p2.getDate().compareTo(p1.getDate()); // Ordre décroissant
        });

        displayedPosts = new ArrayList<>(allPosts);
        refreshPosts();
    }
    private void refreshPosts() {
        refreshPosts(displayedPosts);
    }

    private void refreshPosts(List<Post> posts) {
        cardContainer.getChildren().clear();
        if (posts != null) {
            for (Post p : posts) {
                VBox card = createCard(p);
                cardContainer.getChildren().add(card);
            }
        }
    }

    private VBox createCard(Post post) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setSpacing(12);
        card.getStyleClass().add("card");

        Label titre = new Label(post.getTitre() != null ? post.getTitre() : "Sans titre");
        titre.getStyleClass().add("titre-label");
        titre.setMaxWidth(Double.MAX_VALUE);

        String username = post.getAuteurId() != null ? userService.getUsernameById(post.getAuteurId()) : "N/A";
        Label meta = new Label("Auteur : " + username + " | Date : " +
                (post.getDate() != null ? post.getDate().toString() : "N/A"));
        meta.getStyleClass().add("meta-label");
        meta.setMaxWidth(Double.MAX_VALUE);

        Label contenu = new Label(post.getContenu() != null ? post.getContenu() : "");
        contenu.setWrapText(true);
        contenu.getStyleClass().add("contenu-label");
        contenu.setMaxWidth(Double.MAX_VALUE);

        ImageView imageView = new ImageView();
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            try {
                File file = new File(post.getImage());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.setFitWidth(300);
                    imageView.setFitHeight(200);
                }
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + e.getMessage());
            }
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        Button voirPlusBtn = new Button("Voir plus");
        Button supprimerBtn = new Button("Supprimer");
        Button likeBtn = new Button("👍 " + post.getLikes());
        Button dislikeBtn = new Button("👎 " + post.getDislikes());

        voirPlusBtn.getStyleClass().add("button-voirplus");
        supprimerBtn.getStyleClass().add("button-supprimer");
        likeBtn.getStyleClass().add("action-button");
        dislikeBtn.getStyleClass().add("action-button");

        buttonBox.getChildren().addAll(voirPlusBtn, likeBtn, dislikeBtn, supprimerBtn);

        User currentUser = Session.getCurrentUser();

        likeBtn.setOnAction(e -> {
            if (currentUser != null) {
                service.likePost(post.getId(), currentUser.getId());
                loadPosts();
            }
        });

        dislikeBtn.setOnAction(e -> {
            if (currentUser != null) {
                service.dislikePost(post.getId(), currentUser.getId());
                loadPosts();
            }
        });

        voirPlusBtn.setOnAction(e -> openDetailsWindow(post));

        supprimerBtn.setOnAction(e -> confirmAndDeletePost(post));

        card.getChildren().addAll(titre, meta, contenu);
        if (imageView.getImage() != null) {
            card.getChildren().add(imageView);
        }
        card.getChildren().add(buttonBox);
        card.prefWidthProperty().bind(cardContainer.widthProperty().subtract(40));

        return card;
    }



    private void openDetailsWindow(Post post) {
        try {
            Stage currentStage = (Stage) cardContainer.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDetailsForum.fxml"));
            VBox root = loader.load();

            AdminDetailsForum controller = loader.getController();
            controller.setPost(post);

            Stage detailStage = new Stage();
            detailStage.setTitle("Détails du forum");
            detailStage.setScene(new Scene(root));
            detailStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Erreur lors de l'ouverture des détails du forum : " + ex.getMessage());
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
                User auteur = service.getAuteurByPostId(post.getId());

                // Suppression du post
                service.delete(post);

                // Envoi de l'e-mail à l'auteur
                if (auteur != null && auteur.getEmail() != null) {
                    MailService mailService = new MailService();
                    String subject = "Votre post a été supprimé";
                    String content = "Bonjour " + auteur.getUsername() + ",\n\nVotre post intitulé \"" + post.getTitre() + "\" a été supprimé par l'administrateur.";
                    mailService.envoyerMail(auteur.getEmail(), subject, content);
                }

                // Rafraîchissement de l'affichage
                loadPosts();
            }
        });
    }



    @FXML
    private void handleCreateForum(ActionEvent event) {
        try {
            Stage currentStage = (Stage) createForumButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminAjouterForum.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Créer un Forum");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de l'interface AjouterForum : " + e.getMessage());
        }
    }


    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            displayedPosts = new ArrayList<>(allPosts);
        } else {
            displayedPosts = allPosts.stream()
                    .filter(post ->
                            (post.getTitre() != null && post.getTitre().toLowerCase().contains(query)) ||
                                    (post.getContenu() != null && post.getContenu().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }

        // Conserver le tri actuel après la recherche
        if (sortByDateButton.getText().contains("Date")) {
            sortByDate(null);
        } else if (sortByLikesButton.getText().contains("Likes")) {
            sortByLikes(null);
        } else if (sortByDislikesButton.getText().contains("Dislikes")) {
            sortByDislikes(null);
        } else {
            refreshPosts();
        }
    }

    @FXML
    private void sortByDate(ActionEvent event) {
        isDateAscending = !isDateAscending;

        displayedPosts.sort((p1, p2) -> {
            if (p1.getDate() == null || p2.getDate() == null) return 0;
            return isDateAscending ? p1.getDate().compareTo(p2.getDate())
                    : p2.getDate().compareTo(p1.getDate());
        });

        sortByDateButton.setText(isDateAscending ? "Date ▲" : "Date ▼");
        sortByLikesButton.setText("Likes");
        sortByDislikesButton.setText("Dislikes");

        refreshPosts();
    }

    @FXML
    private void sortByLikes(ActionEvent event) {
        isLikesAscending = !isLikesAscending;

        displayedPosts.sort((p1, p2) ->
                isLikesAscending ? Integer.compare(p1.getLikes(), p2.getLikes())
                        : Integer.compare(p2.getLikes(), p1.getLikes()));

        sortByLikesButton.setText(isLikesAscending ? "Likes ▲" : "Likes ▼");
        sortByDateButton.setText("Date");
        sortByDislikesButton.setText("Dislikes");

        refreshPosts();
    }

    @FXML
    private void sortByDislikes(ActionEvent event) {
        isDislikesAscending = !isDislikesAscending;

        displayedPosts.sort((p1, p2) ->
                isDislikesAscending ? Integer.compare(p1.getDislikes(), p2.getDislikes())
                        : Integer.compare(p2.getDislikes(), p1.getDislikes()));

        sortByDislikesButton.setText(isDislikesAscending ? "Dislikes ▲" : "Dislikes ▼");
        sortByDateButton.setText("Date");
        sortByLikesButton.setText("Likes");

        refreshPosts();
    }
}