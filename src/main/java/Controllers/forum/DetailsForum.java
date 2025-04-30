package Controllers.forum;

import Entites.Post;
import Entites.Commentaire;
import Entites.User;
import Services.CommentaireService;
import Services.PostService;
import Services.UserService;
import Services.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DetailsForum {

    // Composants FXML
    @FXML private Label titreLabel;
    @FXML private Label auteurDateLabel;
    @FXML private TextArea contenuArea;
    @FXML private ImageView imageView;
    @FXML private VBox commentairesBox;
    @FXML private TextField champCommentaire;
    @FXML private Button modifierBtn;
    @FXML private Button likeBtn;
    @FXML private Button dislikeBtn;

    // Services
    private final PostService postService = new PostService();
    private final CommentaireService commentaireService = new CommentaireService();
    private final UserService userService = new UserService();

    private Post post;

    @FXML
    public void initialize() {
        // Configuration initiale si nécessaire
        contenuArea.setWrapText(true);
    }

    public void setPost(Post post) {
        this.post = post;
        loadPostData();
        setupModifierButton();
        loadImage();
        afficherCommentaires();
    }

    private void loadPostData() {
        titreLabel.setText(post.getTitre());

        String username = userService.getUsernameById(post.getAuteurId());
        String dateStr = post.getDate() != null ? post.getDate().toString() : "Date inconnue";
        auteurDateLabel.setText("Publié par " + username + " • " + dateStr);

        contenuArea.setText(post.getContenu());
        updateLikeDislikeButtons();
    }

    private void setupModifierButton() {
        User currentUser = Session.getCurrentUser();
        modifierBtn.setVisible(currentUser != null && currentUser.getId() == post.getAuteurId());
    }

    private void loadImage() {
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            try {
                File file = new File(post.getImage());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), 600, 400, true, true);
                    imageView.setImage(image);

                    // Optionnel: ajustement supplémentaire si nécessaire
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(600);
                    imageView.setFitHeight(400);
                }
            } catch (Exception e) {
                System.out.println("Erreur de chargement de l'image: " + e.getMessage());
                // Optionnel: afficher une image par défaut ou un placeholder
            }
        } else {
            // Cacher l'ImageView s'il n'y a pas d'image
            imageView.setVisible(false);
        }
    }

    private void updateLikeDislikeButtons() {
        likeBtn.setText("👍 " + post.getLikes());
        dislikeBtn.setText("👎 " + post.getDislikes());
    }

    @FXML
    private void handleLike() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            postService.likePost(post.getId(), currentUser.getId());
            refreshPostData();
        } else {
            showAlert("Connexion requise", "Vous devez être connecté pour voter", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDislike() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            postService.dislikePost(post.getId(), currentUser.getId());
            refreshPostData();
        } else {
            showAlert("Connexion requise", "Vous devez être connecté pour voter", Alert.AlertType.WARNING);
        }
    }

    private void refreshPostData() {
        post = postService.getPostById(post.getId());
        updateLikeDislikeButtons();
    }

    private void afficherCommentaires() {
        commentairesBox.getChildren().clear();

        List<Commentaire> commentaires = commentaireService.getCommentairesParPostId(post.getId());
        User currentUser = Session.getCurrentUser();

        for (Commentaire commentaire : commentaires) {
            VBox commentCard = createCommentCard(commentaire, currentUser);
            commentairesBox.getChildren().add(commentCard);
        }
    }

    private VBox createCommentCard(Commentaire commentaire, User currentUser) {
        VBox card = new VBox(8);
        card.getStyleClass().add("comment-card");
        card.setPadding(new javafx.geometry.Insets(12));

        // En-tête du commentaire
        HBox headerBox = new HBox(10);
        String username = userService.getUsernameById(commentaire.getAuteurId());
        Label authorLabel = new Label(username);
        authorLabel.getStyleClass().add("comment-author");

        String dateStr = commentaire.getDate() != null ? commentaire.getDate().toString() : "";
        Label dateLabel = new Label(dateStr);
        dateLabel.getStyleClass().add("comment-date");

        headerBox.getChildren().addAll(authorLabel, dateLabel);

        // Contenu du commentaire
        Label contentLabel = new Label(commentaire.getContenu());
        contentLabel.getStyleClass().add("comment-content");
        contentLabel.setWrapText(true);

        // Boutons d'action
        HBox actionBox = new HBox(10);
        if (currentUser != null && currentUser.getId() == commentaire.getAuteurId()) {
            Button editBtn = new Button("Modifier");
            editBtn.getStyleClass().add("action-button");
            editBtn.setOnAction(e -> handleEditComment(commentaire, contentLabel));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("secondary-button");
            deleteBtn.setOnAction(e -> handleDeleteComment(commentaire));

            actionBox.getChildren().addAll(editBtn, deleteBtn);
        }

        Button translateBtn = new Button("Traduire");
        translateBtn.getStyleClass().add("action-button");
        translateBtn.setOnAction(e -> handleTranslateComment(contentLabel, commentaire.getContenu()));

        actionBox.getChildren().add(translateBtn);

        card.getChildren().addAll(headerBox, contentLabel, actionBox);
        return card;
    }

    private void handleEditComment(Commentaire commentaire, Label contentLabel) {
        TextInputDialog dialog = new TextInputDialog(contentLabel.getText());
        dialog.setTitle("Modifier le commentaire");
        dialog.setHeaderText("Modifiez votre commentaire");
        dialog.setContentText("Nouveau contenu:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                try {
                    commentaire.setContenu(newContent);
                    commentaireService.modifierCommentaire(commentaire);
                    afficherCommentaires();
                } catch (IllegalArgumentException e) {
                    showAlert("Contenu inapproprié", "Votre commentaire contient des mots inappropriés.", Alert.AlertType.WARNING);
                }
            }
        });
    }

    private void handleDeleteComment(Commentaire commentaire) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer ce commentaire ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer définitivement ce commentaire ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            commentaireService.supprimerCommentaire(commentaire.getId());
            afficherCommentaires();
        }
    }

    private void handleTranslateComment(Label targetLabel, String originalText) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("English", List.of("English", "Français", "Arabic"));
        dialog.setTitle("Traduction");
        dialog.setHeaderText("Choisissez la langue de traduction");
        dialog.setContentText("Langue:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(language -> {
            try {
                String targetLang = getLanguageCode(language);
                String translatedText = translateText(originalText, "fr", targetLang);
                targetLabel.setText(translatedText);
            } catch (IOException e) {
                showAlert("Erreur de traduction", "Impossible de traduire le texte: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void ajouterCommentaire() {
        String content = champCommentaire.getText().trim();
        if (content.isEmpty()) {
            showAlert("Champ vide", "Veuillez écrire un commentaire", Alert.AlertType.WARNING);
            return;
        }

        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Vous devez être connecté pour commenter", Alert.AlertType.WARNING);
            return;
        }

        Commentaire newComment = new Commentaire(
                post.getId(),
                content,
                currentUser.getId(),
                new Date()
        );

        try {
            commentaireService.ajouterCommentaire(newComment);
            champCommentaire.clear();
            afficherCommentaires();
        } catch (IllegalArgumentException e) {
            showAlert("Contenu inapproprié", "Votre commentaire contient des mots inappropriés", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeForum.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Forum");

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) titreLabel.getScene().getWindow();
            currentStage.close();

            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la liste des posts", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPost.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            ModifierPost controller = loader.getController();
            controller.setPost(post);

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) titreLabel.getScene().getWindow();
            currentStage.close();

            stage.setTitle("Modifier le post");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur de post", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void traduireContenuPost() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("English", List.of("English", "Français", "Arabic"));
        dialog.setTitle("Traduction");
        dialog.setHeaderText("Choisissez la langue de traduction");
        dialog.setContentText("Langue:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(language -> {
            try {
                String targetLang = getLanguageCode(language);
                String translatedContent = translateText(post.getContenu(), "fr", targetLang);
                String translatedTitle = translateText(post.getTitre(), "fr", targetLang);

                contenuArea.setText(translatedContent);
                titreLabel.setText(translatedTitle);
            } catch (IOException e) {
                showAlert("Erreur de traduction", "Impossible de traduire le contenu: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Méthodes utilitaires
    private String translateText(String text, String sourceLang, String targetLang) throws IOException {
        String encodedText = encodeURIComponent(text);
        String apiUrl = "https://lingva.ml/api/v1/" + sourceLang + "/" + targetLang + "/" + encodedText;

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());
            return json.getString("translation");
        }
    }

    private String encodeURIComponent(String s) {
        try {
            return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private String getLanguageCode(String language) {
        return switch (language) {
            case "Français" -> "fr";
            case "Arabic" -> "ar";
            default -> "en";
        };
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}