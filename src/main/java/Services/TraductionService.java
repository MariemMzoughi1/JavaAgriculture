package Services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TraductionService {

    public static String traduireTexte(String texte, String sourceLangue, String cibleLangue) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String body = "q=" + URLEncoder.encode(texte, StandardCharsets.UTF_8)
                + "&source=" + sourceLangue
                + "&target=" + cibleLangue
                + "&format=text";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.mymemory.translated.net/translate"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Simple parsing du résultat JSON
        String responseBody = response.body();
        int index = responseBody.indexOf("\"translatedText\":\"") + "\"translatedText\":\"".length();
        int endIndex = responseBody.indexOf("\"", index);
        return responseBody.substring(index, endIndex);
    }
}
