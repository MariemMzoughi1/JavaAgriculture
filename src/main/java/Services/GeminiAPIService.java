package Services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeminiAPIService {
    private static final String API_KEY = "AIzaSyCONIr7_lJ_KX4v_tgoyThSgR_KB7kv7oM"; // Mets ta clé API ici
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    // ✅ Détecter mauvais mots
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

            OutputStream os = conn.getOutputStream();
            os.write(inputJson.getBytes());
            os.flush();

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            String reponseString = response.toString().toLowerCase();
            return reponseString.contains("oui");

        } catch (Exception e) {
            System.out.println("❌ Erreur Gemini API (bad words) : " + e.getMessage());
            return false;
        }
    }

    // ✅ Détecter si lié à l'agriculture
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

            OutputStream os = conn.getOutputStream();
            os.write(inputJson.getBytes());
            os.flush();

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            String reponseString = response.toString().toLowerCase();
            return reponseString.contains("oui");

        } catch (Exception e) {
            System.out.println("❌ Erreur Gemini API (agriculture) : " + e.getMessage());
            return false;
        }
    }
}
