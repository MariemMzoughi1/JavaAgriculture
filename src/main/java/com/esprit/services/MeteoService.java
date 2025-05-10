package com.esprit.services;

import com.esprit.utils.SSLBypass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MeteoService {

    private static final String API_KEY = "e756aae0000ede79a1eacba329ac8b74";

    public String getMeteoData(String ville) {
        try {
            // Désactiver SSL uniquement pour cette requête
            SSLBypass.disableCertificateValidation();


            String urlStr = "https://api.openweathermap.org/data/2.5/forecast?q=" +
                    java.net.URLEncoder.encode(ville, StandardCharsets.UTF_8) +
                    "&units=metric&appid=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            in.close();
            conn.disconnect();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
