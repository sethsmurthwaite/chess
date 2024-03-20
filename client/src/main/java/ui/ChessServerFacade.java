package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class ChessServerFacade {
    static Gson gson = new Gson();
    private static final String URL = "http://localhost:8080";
    private static final HttpClient httpClient = HttpClient.newHttpClient();



    public static AuthData register(UserData user) throws IOException, InterruptedException {
        AuthData auth;

        String requestBody = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
             auth = gson.fromJson(response.body(), AuthData.class);
        } else if (response.statusCode() == 403) {
            throw new Error("User already exists");
        } else {
            throw new IOException("Failed to register: " + response.statusCode() + " " + response.body());
        }

        return auth;
    }
    public AuthData login(UserData user) throws IOException, InterruptedException {
        String requestBody = gson.toJson(user);

        AuthData auth;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            auth = gson.fromJson(response.body(), AuthData.class);
        } else if (response.statusCode() == 401) {
            auth = null;
            throw new Error("Invalid login credentials");
        } else {
            auth = null;
            System.out.println("Something went wrong in serverfacade login()");
        }
        return auth;
    }
    public void logout(AuthData auth) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/session"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .DELETE().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Failed to logout: " + response.statusCode() + " " + response.body());
    }
    public void join(AuthData auth, String color, GameData game) throws IOException, InterruptedException {
        JsonObject obj = new JsonObject();
        obj.addProperty("playerColor", color);
        obj.addProperty("gameID", game.gameID());
        String requestBody = obj.toString();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Failed to join game: " + response.statusCode() + " " + response.body());
    }
    public void create(String gameName, AuthData auth) throws IOException, InterruptedException {
        String requestBody = new Gson().toJson(Map.of("gameName", gameName));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Error("Something went wrong in serverfacade create(): " + response.statusCode());
        }
    }
    public GameList list(AuthData auth) throws IOException, InterruptedException {
        GameList gameList = null;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Error("Something went wrong in serverfacade list(): " + response.statusCode());
        }
        gameList = gson.fromJson(response.body(), GameList.class);

        return gameList;
    }

}
