package Controllers.forum;

import Services.GeminiAPIService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChatbotController {

    @FXML
    private TextArea chatDisplay;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        sendButton.setOnAction(event -> {
            String question = userInput.getText().trim();
            if (!question.isEmpty()) {
                chatDisplay.appendText("Vous : " + question + "\n");
                userInput.clear();

                new Thread(() -> {
                    String reponse = GeminiAPIService.obtenirReponseChatbot(question);
                    Platform.runLater(() -> chatDisplay.appendText("Bot : " + reponse + "\n"));
                }).start();
            }
        });
    }
}
