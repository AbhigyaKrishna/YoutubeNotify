package com.aerialdev.youtubenotify;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class YoutubeUserService {

    private final String apiKey;
    private final String user;
    private String userId = "";
    private String playlistId = "";
    private JsonObject playlistData = null;

    public YoutubeUserService(String apiKey, String user) {
        this.apiKey = apiKey;
        this.user = user;
        try {
            this.userId = this.getUserId();
            this.playlistId = this.getPlayListId();
            this.playlistData = this.getPlayListData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getUserId() throws IOException {
        URL url = new URL("https://www.googleapis.com/youtube/v3/channels?part=id&forUsername=" + this.user + "&key=" + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");
        connection.connect();
        JsonObject json = this.parseJson(connection.getInputStream());
        connection.disconnect();
        return json.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonPrimitive("id").getAsString();
    }

    protected String getPlayListId() throws IOException {
        URL url = new URL("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id=" + this.userId + "&key=" + this.apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");
        connection.connect();
        JsonObject json = this.parseJson(connection.getInputStream());
        connection.disconnect();
        return json.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("contentDetails").getAsJsonObject("relatedPlaylists")
                .getAsJsonPrimitive("uploads").getAsString();
    }

    protected JsonObject getPlayListData() throws IOException {
        URL url = new URL("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id=" + this.playlistId + "&key=" + this.apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");
        connection.connect();
        JsonObject json = this.parseJson(connection.getInputStream());
        connection.disconnect();
        return json;
    }

    private JsonObject parseJson(InputStream in) throws IOException {
        Scanner sc = new Scanner(in);
        String text = "";
        while (sc.hasNext()) {
            text = text + sc.nextLine();
        }
        sc.close();
        in.close();

        JsonElement element = JsonParser.parseString(text);
        return element.getAsJsonObject();
    }

}
