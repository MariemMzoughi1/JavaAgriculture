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
    private void initialize() {
        // Méthode appelée automatiquement après le chargement de FXML (facultatif ici)
    }

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

        List<Commentaire> commentaires = service.getCommentairesParPostId(post.getId());
        commentairesBox.getChildren().clear();

        int currentUserId = Session.getCurrentUser().getId();

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
                        afficherAlerte("Contenu inapproprié", "Votre commentaire contient des mots ou expressions inappropriés.", Alert.AlertType.WARNING);
                    }
                }
            });

            supprimerBtn.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation de suppression");
                confirmation.setHeaderText("Voulez-vous vraiment supprimer ce commentaire ?");
                confirmation.setContentText("Cette action est irréversible.");

                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    service.supprimerCommentaire(c.getId());
                    afficherCommentaires();
                }
            });

            traduireBtn.setOnAction(e -> {
                try {
                    String texteTraduit = traduireTexte(c.getContenu(), "fr", "en");
                    contenuField.setText(texteTraduit);
                } catch (IOException ex) {
                    afficherAlerte("Erreur de traduction", "Erreur lors de la traduction : " + ex.getMessage(), Alert.AlertType.ERROR);
                }
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
            afficherAlerte("Champ vide", "Veuillez écrire quelque chose avant de publier.", Alert.AlertType.WARNING);
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
            afficherAlerte("Contenu inapproprié", "Votre commentaire contient des mots ou expressions inappropriés.", Alert.AlertType.WARNING);
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
            afficherAlerte("Erreur", "Erreur lors du chargement de la liste des forums.", Alert.AlertType.ERROR);
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
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la modification du post.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String traduireTexte(String texte, String sourceLang, String targetLang) throws IOException {
        String encodedTexte = encodeURIComponent(texte);
        String apiUrl = "https://lingva.ml/api/v1/" + sourceLang + "/" + targetLang + "/" + encodedTexte;
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream inputStream = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());
            return json.getString("translation");
        }
    }
    private static String encodeURIComponent(String s) {
        try {
            return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20")  // remplacer les + par %20
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }


    @FXML
    private void traduireContenuPost() {
        try {
            String contenuTraduit = traduireTexte(post.getContenu(), "fr", "en");
            String titreTraduit = traduireTexte(post.getTitre(), "fr", "en");

            contenuArea.setText(contenuTraduit);
            titreLabel.setText(titreTraduit);
        } catch (IOException e) {
            afficherAlerte("Erreur de traduction", "Erreur lors de la traduction du post.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
