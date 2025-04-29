package controles;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuPrincipalController {

    @FXML
    private StackPane mainContent;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox menuVBox;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    private boolean sidebarOpen = true;

    @FXML
    public void initialize() {
        // Horloge temps réel
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
            timeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void toggleSidebar() {
        if (sidebarOpen) {
            sidebar.setPrefWidth(60);
            for (Node node : menuVBox.getChildren()) {
                if (node instanceof Button button && button != toggleSidebarButton) {
                    button.setText("");
                    button.setPrefWidth(40);
                }
            }
            sidebarOpen = false;
        } else {
            sidebar.setPrefWidth(220);
            int i = 0;
            for (Node node : menuVBox.getChildren()) {
                if (node instanceof Button button && button != toggleSidebarButton) {
                    switch (i) {
                        case 0 -> button.setText("📦 Produits");
                        case 1 -> button.setText("🛒 Commandes");
                    }
                    button.setPrefWidth(180);
                    i++;
                }
            }
            sidebarOpen = true;
        }
    }

    @FXML
    private void ouvrirProduits() {
        chargerVue("AfficherProduit.fxml");
    }

    @FXML
    private void ouvrirCommandes() {
        chargerVue("commande.fxml");
    }

    private void chargerVue(String vue) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + vue));
            Node content = loader.load();
            mainContent.getChildren().setAll(content);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Fichier FXML introuvable : " + vue);
        }
    }

}
