package controllers;

import entities.Commentaire;
import entities.Post;
import entities.User;
import org.json.JSONObject;
import services.CommentaireService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.PostService;
import services.Session;
import services.UserService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DetailsForum {

    @FXML private Label titreLabel;
    @FXML private Label auteurDateLabel;
    @FXML private TextArea contenuArea;
    @FXML private ImageView imageView;
    @FXML private VBox commentairesBox;
    @FXML private TextField champCommentaire;
    @FXML private Button modifierBtn;
    @FXML private Button likeBtn;
    @FXML private Button dislikeBtn;

    private final PostService postService = new PostService();
    private final CommentaireService commentaireService = new CommentaireService();
    private final UserService userService = new UserService();

    private Post post;

    // 🔁 Chemin local vers Symfony (à adapter selon ton projet)
    private static final String SYMFONY_IMAGE_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/posts/";

    @FXML
    public void initialize() {
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

        String email = userService.getEmailById(post.getAuteurId());
        String dateStr = post.getDate() != null ? post.getDate().toString() : "Date inconnue";
        auteurDateLabel.setText("Publié par " + email + " • " + dateStr);

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
                File file = new File(SYMFONY_IMAGE_DIR + post.getImage());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), 600, 400, true, true);
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(600);
                    imageView.setFitHeight(400);
                } else {
                    System.out.println("Image introuvable à : " + file.getAbsolutePath());
                    imageView.setVisible(false);
                }
            } catch (Exception e) {
                System.out.println("Erreur de chargement de l'image: " + e.getMessage());
                imageView.setVisible(false);
            }
        } else {
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

        HBox headerBox = new HBox(10);
        String username = userService.getEmailById(commentaire.getAuteurId());
        Label authorLabel = new Label(username);
        Label dateLabel = new Label(commentaire.getDate() != null ? commentaire.getDate().toString() : "");

        headerBox.getChildren().addAll(authorLabel, dateLabel);

        Label contentLabel = new Label(commentaire.getContenu());
        contentLabel.setWrapText(true);

        HBox actionBox = new HBox(10);
        if (currentUser != null && currentUser.getId() == commentaire.getAuteurId()) {
            Button editBtn = new Button("Modifier");
            Button deleteBtn = new Button("Supprimer");

            editBtn.setOnAction(e -> handleEditComment(commentaire, contentLabel));
            deleteBtn.setOnAction(e -> handleDeleteComment(commentaire));

            actionBox.getChildren().addAll(editBtn, deleteBtn);
        }

        Button translateBtn = new Button("Traduire");
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
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

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
                showAlert("Erreur", "Erreur de traduction : " + e.getMessage(), Alert.AlertType.ERROR);
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
            showAlert("Connexion requise", "Vous devez être connecté", Alert.AlertType.WARNING);
            return;
        }

        Commentaire commentaire = new Commentaire(post.getId(), content, currentUser.getId(), new Date());

        try {
            commentaireService.ajouterCommentaire(commentaire);
            champCommentaire.clear();
            afficherCommentaires();
        } catch (IllegalArgumentException e) {
            showAlert("Inapproprié", "Votre commentaire contient des mots inappropriés", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeForum.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Forum");

            ((Stage) titreLabel.getScene().getWindow()).close();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ModifierPost.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            ModifierPost controller = loader.getController();
            controller.setPost(post);

            ((Stage) titreLabel.getScene().getWindow()).close();
            stage.setTitle("Modifier le post");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage(), Alert.AlertType.ERROR);
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
                showAlert("Erreur", "Erreur de traduction : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

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
                    .replaceAll("\\+", "%20");
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
