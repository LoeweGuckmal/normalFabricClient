package ch.loewe.normal_use_client.fabricclient.cape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MinecraftApi {
    public MinecraftApi() {
    }

    public static UUID getUUID(String username) {
        //logger.warn("Making an API call for {}", username);
        JsonObject playerElement = getApiData(username);
        if (playerElement != null) {
            JsonElement playerUUID = playerElement.get("full_uuid");
            if (playerUUID != null && !playerUUID.isJsonNull()) {
                //logger.warn("{} ({}) was found", username, playerUUID);
                return UUID.fromString(playerUUID.getAsString());
            }
        }

        //logger.warn("{} was not found", username);
        return null;
    }

    private static JsonObject getApiData(String data) {
        try {
            URL url = new URL("https://minecraftapi.net/api/v1/profile/" + data);
            HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();
            if (httpurlconnection.getResponseCode() / 100 != 2) {
                return null;
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
                StringBuilder response = new StringBuilder();

                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return JsonParser.parseString(response.toString()).getAsJsonObject();
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }
}
