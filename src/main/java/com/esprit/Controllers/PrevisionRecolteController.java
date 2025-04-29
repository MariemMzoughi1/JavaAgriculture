package com.esprit.Controllers;

import com.esprit.services.CultureService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PrevisionRecolteController {

    @FXML private TextField nomCultureField;
    @FXML private TextField superficieField;
    @FXML private TextField saisonField;
    @FXML private Label resultLabel;

    private final CultureService cultureService = new CultureService();

    @FXML
    void calculerRecolte() {
        try {
            String nomCulture = nomCultureField.getText().trim();
            double superficieHa = Double.parseDouble(superficieField.getText().trim());
            String saison = saisonField.getText().trim();

            if (nomCulture.isEmpty() || saison.isEmpty()) {
                resultLabel.setText("❌ Veuillez remplir tous les champs.");
                return;
            }

            double estimation = cultureService.estimerRecolteParCulture(nomCulture, superficieHa, saison);
            resultLabel.setText("🌾 Récolte estimée : " + String.format("%.2f", estimation) + " tonnes");

        } catch (NumberFormatException e) {
            resultLabel.setText("⚠️ Entrez une superficie valide (en hectares).");
        }
    }
}
