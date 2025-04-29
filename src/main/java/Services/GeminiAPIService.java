package Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GeminiAPIService {
    private static final String API_KEY = "AIzaSyCONIr7_lJ_KX4v_tgoyThSgR_KB7kv7oM"; // Mets ta clé API ici
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static String obtenirReponseChatbot(String question) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // 🧠 Prompt pour forcer la réponse sur des sujets agricoles uniquement
            String prompt = "Tu es un assistant expert en agriculture. Ne réponds qu'aux questions en lien avec l'agriculture (comme les cultures, les engrais, la gestion de l'eau, les maladies des plantes, etc.). "
                    + "Si une question ne concerne pas l'agriculture, réponds uniquement : 'Je suis un assistant spécialisé en agriculture. Veuillez poser une question en lien avec ce domaine.'\n"
                    + "Question : " + question;

            String inputJson = "{"
                    + "\"contents\": [{"
                    + "\"parts\": [{\"text\": \"" + escapeJson(prompt) + "\"}]"
                    + "}]"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = inputJson.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // Extraction simple
            String resStr = response.toString();
            int start = resStr.indexOf("\"text\":");
            if (start != -1) {
                int firstQuote = resStr.indexOf("\"", start + 7);
                int secondQuote = resStr.indexOf("\"", firstQuote + 1);
                if (firstQuote != -1 && secondQuote != -1) {
                    return resStr.substring(firstQuote + 1, secondQuote)
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"");
                }
            }

            return "Désolé, je n'ai pas pu comprendre la réponse.";

        } catch (Exception e) {
            System.out.println("❌ Erreur Gemini API : " + e.getMessage());
            return "Erreur de connexion avec le chatbot.";
        }
    }

    public static boolean contientBadWords(String texte) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String inputJson = "{"
                    + "\"contents\": [{"
                    + "\"parts\": [{\"text\": \"Est-ce que ce texte contient des propos offensants ou inappropriés ? Réponds uniquement par 'oui' ou 'non' : \\n" + texte + "\"}]"
                    + "}]"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(inputJson.getBytes());
                os.flush();
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return response.toString().toLowerCase().contains("oui");

        } catch (Exception e) {
            System.out.println("❌ Erreur Gemini API (bad words) : " + e.getMessage());
            return false;
        }
    }

    public static boolean estLieAAgriculture(String texte) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String inputJson = "{"
                    + "\"contents\": [{"
                    + "\"parts\": [{\"text\": \"Ce texte est-il lié à l'agriculture ? Réponds uniquement par 'oui' ou 'non' : \\n" + texte + "\"}]"
                    + "}]"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(inputJson.getBytes());
                os.flush();
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return response.toString().toLowerCase().contains("oui");

        } catch (Exception e) {
            System.out.println("❌ Erreur Gemini API (agriculture) : " + e.getMessage());
            return false;
        }
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
