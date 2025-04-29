package Controllers.forum;

import Entites.Post;
import Entites.Commentaire;
import Services.CommentaireService;
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

    @FXML private Label titreLabel;
    @FXML private Label auteurDateLabel;
    @FXML private TextArea contenuArea;
    @FXML private Label likesDislikesLabel;
    @FXML private ImageView imageView;
    @FXML private VBox commentairesBox;
    @FXML private TextField champCommentaire;

    private Post post;

    @FXML
    private void initialize() {}

    public void setPost(Post post) {
        this.post = post;

        UserService userService = new UserService();
        String auteurUsername = userService.getUsernameById(post.getAuteurId());

        titreLabel.setText(post.getTitre());
        auteurDateLabel.setText("Auteur : " + auteurUsername + " | Date : " + post.getDate());
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

    private void afficherCommentaires() {
        CommentaireService service = new CommentaireService();
        UserService userService = new UserService();
        int currentUserId = Session.getCurrentUser().getId();

        List<Commentaire> commentaires = service.getCommentairesParPostId(post.getId());
        commentairesBox.getChildren().clear();

        for (Commentaire c : commentaires) {
            VBox commentaireCard = new VBox(5);
            commentaireCard.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 10; -fx-background-radius: 8;");

            String username = userService.getUsernameById(c.getAuteurId());
            Label auteurLabel = new Label("Auteur : " + username);

            TextField contenuField = new TextField(c.getContenu());
            contenuField.setEditable(false);
            contenuField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

            HBox boutons = new HBox(10);
            Button modifierBtn = new Button("Modifier");
            Button enregistrerBtn = new Button("Enregistrer");
            Button supprimerBtn = new Button("Supprimer");
            Button traduireBtn = new Button("Traduire");

            enregistrerBtn.setVisible(false);

            if (currentUserId != c.getAuteurId()) {
                modifierBtn.setDisable(true);
                supprimerBtn.setDisable(true);
            }

            modifierBtn.setOnAction(e -> {
                contenuField.setEditable(true);
                contenuField.setStyle("");
                enregistrerBtn.setVisible(true);
                modifierBtn.setVisible(false);
            });

            enregistrerBtn.setOnAction(e -> {
                String nouveauTexte = contenuField.getText().trim();
                if (!nouveauTexte.isEmpty()) {
                    c.setContenu(nouveauTexte);
                    try {
                        service.modifierCommentaire(c);
                        afficherCommentaires();
                    } catch (IllegalArgumentException ex) {
                        afficherAlerte("Contenu inapproprié", "Votre commentaire contient des mots inappropriés.", Alert.AlertType.WARNING);
                    }
                }
            });

            supprimerBtn.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Suppression");
                confirmation.setHeaderText("Confirmer la suppression");
                confirmation.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");

                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    service.supprimerCommentaire(c.getId());
                    afficherCommentaires();
                }
            });

            traduireBtn.setOnAction(e -> {
                ChoiceDialog<String> dialog = new ChoiceDialog<>("English", "English", "Arabic", "Français");
                dialog.setTitle("Traduction");
                dialog.setHeaderText("Traduire le commentaire");
                dialog.setContentText("Langue cible :");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(langueChoisie -> {
                    String codeLangue = getCodeLangue(langueChoisie);
                    try {
                        String texteTraduit = traduireTexte(c.getContenu(), "fr", codeLangue);
                        contenuField.setText(texteTraduit);
                    } catch (IOException ex) {
                        afficherAlerte("Erreur de traduction", "Échec de la traduction : " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            });

            boutons.getChildren().addAll(modifierBtn, enregistrerBtn, supprimerBtn, traduireBtn);
            commentaireCard.getChildren().addAll(auteurLabel, contenuField, boutons);
            commentairesBox.getChildren().add(commentaireCard);
        }
    }

    @FXML
    private void ajouterCommentaire() {
        String contenu = champCommentaire.getText().trim();
        if (contenu.isEmpty()) {
            afficherAlerte("Champ vide", "Veuillez écrire un commentaire.", Alert.AlertType.WARNING);
            return;
        }

        int utilisateurId = Session.getCurrentUser().getId();
        Commentaire commentaire = new Commentaire(post.getId(), contenu, utilisateurId, new Date());
        CommentaireService service = new CommentaireService();

        try {
            service.ajouterCommentaire(commentaire);
            champCommentaire.clear();
            afficherCommentaires();
        } catch (IllegalArgumentException e) {
            afficherAlerte("Contenu inapproprié", "Commentaire inapproprié détecté.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleRetour() {
        Stage stage = (Stage) titreLabel.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeForum.fxml"));
            Stage listestage = new Stage();
            listestage.setTitle("Liste des Forums");
            listestage.setScene(new Scene(loader.load()));
            listestage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Chargement de la liste échoué.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPost.fxml"));
            VBox root = loader.load();

            ModifierPost controller = loader.getController();
            controller.setPost(post);

            Stage stage = new Stage();
            stage.setTitle("Modifier le Post");
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) titreLabel.getScene().getWindow()).close();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Ouverture de la modification échouée.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void traduireContenuPost() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("English", "English", "Arabic", "Français");
        dialog.setTitle("Traduction");
        dialog.setHeaderText("Traduire le contenu du post");
        dialog.setContentText("Langue cible :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(langueChoisie -> {
            String codeLangue = getCodeLangue(langueChoisie);
            try {
                String texteTraduit = traduireTexte(post.getContenu(), "fr", codeLangue);
                contenuArea.setText(texteTraduit);

                String titreTraduit = traduireTexte(post.getTitre(), "fr", codeLangue);
                titreLabel.setText(titreTraduit);
            } catch (IOException e) {
                afficherAlerte("Erreur", "Traduction impossible : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private String traduireTexte(String texte, String sourceLang, String targetLang) throws IOException {
        String encodedTexte = encodeURIComponent(texte);
        String apiUrl = "https://lingva.ml/api/v1/" + sourceLang + "/" + targetLang + "/" + encodedTexte;
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

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

    private String getCodeLangue(String langue) {
        return switch (langue) {
            case "English" -> "en";
            case "Arabic" -> "ar";
            case "Français" -> "fr";
            default -> "en";
        };
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
