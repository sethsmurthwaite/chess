package ui;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Map;

public class ChessServerFacade {
    static Gson gson = new Gson();
    private static String url;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public ChessServerFacade(int port) {
        url = "http://localhost:" + port;
    }

    public static AuthData register(UserData user) throws IOException, InterruptedException {
        AuthData auth;

        String requestBody = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/user"))
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
    public static AuthData login(UserData user) throws IOException, InterruptedException {
        String requestBody = gson.toJson(user);

        AuthData auth;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/session"))
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
    public static void logout(AuthData auth) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/session"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .DELETE().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Failed to logout: " + response.statusCode() + " " + response.body());
    }
    public static void join(AuthData auth, String color, GameData game) throws IOException, InterruptedException {
        JsonObject obj = new JsonObject();
        obj.addProperty("playerColor", color);
        obj.addProperty("gameID", game.gameID());
        String requestBody = obj.toString();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Failed to join game: " + response.statusCode() + " " + response.body());
    }
    public static void create(String gameName, AuthData auth) throws IOException, InterruptedException {
        String requestBody = new Gson().toJson(Map.of("gameName", gameName));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", auth.authToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Error("Something went wrong in serverfacade create(): " + response.statusCode());
        }
    }
    public static GameList list(AuthData auth) throws IOException, InterruptedException {
        GameList gameList = null;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/game"))
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
    public static HashSet<ChessMove> getValidMoves(String authToken, ChessPosition pos, Integer gameID) throws IOException, InterruptedException {
        GameMoveCollection validMoves = null;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/move"))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .header("Position", gson.toJson(pos))
                .header("gameID", gson.toJson(gameID))
                .GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Error("Something went wrong in server facade highlight(): " + response.statusCode());
        }
        validMoves = gson.fromJson(response.body(), GameMoveCollection.class);

        return validMoves.getValidMoves();
    }
}
