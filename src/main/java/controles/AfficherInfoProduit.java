package controles;

import Entites.Produit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AfficherInfoProduit {

    @FXML
    private Label labelId;

    @FXML
    private Label labelNom;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelDescription;

    @FXML
    private Label labelCategorie;

    @FXML
    private Label labelPrix;

    @FXML
    private Label labelQuantiteStock;

    @FXML
    private Label labelAgriculteurId;

    // Champs de texte pour modification
    private TextField textFieldNom;
    private TextField textFieldDescription;
    private TextField textFieldPrix;
    private TextField textFieldQuantiteStock;

    // Cette méthode initialise les informations du produit
    public void initialize(Produit produit) {
        if (produit != null) {
            labelId.setText(String.valueOf(produit.getId()));
            labelNom.setText(produit.getNom());
            labelDate.setText(produit.getDate_ajout().toString());
            labelDescription.setText(produit.getDescription());
            labelCategorie.setText(produit.getCategorie());
            labelPrix.setText(String.valueOf(produit.getPrix_unitaire()));
            labelQuantiteStock.setText(String.valueOf(produit.getQuantite_stock()));
            labelAgriculteurId.setText(String.valueOf(produit.getAgriculteur_id()));
        }
    }

    // Méthode pour fermer la fenêtre
    @FXML
    private void onClose() {
        Stage stage = (Stage) labelNom.getScene().getWindow();
        stage.close();
    }

    // Méthode pour activer la modification des informations du produit
    @FXML
    private void onEditClicked() {
        // Remplacer les labels par des champs de texte pour modification
        textFieldNom = new TextField(labelNom.getText());
        textFieldDescription = new TextField(labelDescription.getText());
        textFieldPrix = new TextField(labelPrix.getText());
        textFieldQuantiteStock = new TextField(labelQuantiteStock.getText());

        // Remplacer les labels par des champs de texte dans l'interface
        labelNom.setGraphic(textFieldNom);
        labelDescription.setGraphic(textFieldDescription);
        labelPrix.setGraphic(textFieldPrix);
        labelQuantiteStock.setGraphic(textFieldQuantiteStock);

        // Créer un bouton pour sauvegarder les modifications
        Button saveButton = new Button("Sauvegarder");
        saveButton.setOnAction(e -> saveChanges());
        // Ajouter le bouton dans le layout (par exemple, à côté des champs)
        // Ajouter le bouton "Sauvegarder" à un conteneur (HBox ou VBox) dans ton FXML
    }

    // Méthode pour sauvegarder les modifications
    @FXML
    private void saveChanges() {
        // Récupérer les valeurs modifiées
        String newNom = textFieldNom.getText();
        String newDescription = textFieldDescription.getText();
        String newPrix = textFieldPrix.getText();
        String newQuantiteStock = textFieldQuantiteStock.getText();

        // Mettre à jour les labels avec les nouvelles valeurs
        labelNom.setText(newNom);
        labelDescription.setText(newDescription);
        labelPrix.setText(newPrix);
        labelQuantiteStock.setText(newQuantiteStock);

        // Mettre à jour le produit dans la base de données ou le modèle
        // Produit produit = ... // Récupérer l'objet produit et mettre à jour ses informations

        // Revenir aux labels après la modification
        labelNom.setGraphic(null);
        labelDescription.setGraphic(null);
        labelPrix.setGraphic(null);
        labelQuantiteStock.setGraphic(null);
    }
}
