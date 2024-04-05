package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DBAuthDAO;
import dataAccess.DatabaseManager;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import service.*;
import model.*;
import spark.*;
import java.util.HashSet;
import org.eclipse.jetty.websocket.api.annotations.*;

import javax.websocket.OnOpen;

import static org.glassfish.grizzly.http.util.Header.Connection;

@WebSocket
public class Server {

    Gson gson = new Gson();
//    MemoryAuthDAO authDAO = new MemoryAuthDAO();
    DatabaseManager dbman = new DatabaseManager();
    DBAuthDAO authDAO = new DBAuthDAO(dbman);
    UserService userService = new UserService(authDAO, dbman);
    GameService gameService = new GameService(authDAO, dbman);

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.webSocket("/connect", Server.class);
        Spark.staticFiles.location("web");
        registerEndpoints();
        try {
            dbman.configureDatabase();
        } catch (DataAccessException ignored) {}
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void registerEndpoints() {
        Spark.get("/health", (req, res) -> "boomshakalaka");
        Spark.post("/user", this::registerUserEndpoint);
        Spark.post("/session", this::loginEndpoint);
        Spark.delete("/session", this::logoutEndpoint);
        Spark.get("/game", this::listGamesEndpoint);
        Spark.post("/game", this::createGameEndpoint);
        Spark.put("/game", this::joinGameEndpoint);
        Spark.delete("/db", this::clearDBEndpoint);
        Spark.exception(DataAccessException.class, this::exceptionHandler);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("WebSocket connected: " + session.getRemoteAddress().getAddress());

        session.getRemote().sendString("You are now connected to the server.");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received message from client: " + message);

        session.getRemote().sendString("Server received your message: " + message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }





    private Object registerUserEndpoint(Request req, Response res) {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        try {
            AuthData authData = userService.register(userData);
            res.status(200);
            return gson.toJson(authData);
        } catch (DataAccessException e) {
            return handleError(res, e);
        }
    }

    private Object loginEndpoint(Request req, Response res) {
        UserData user = gson.fromJson(req.body(), UserData.class);
        try {
            AuthData authData = userService.login(user);
            res.status(200);
            return gson.toJson(authData);
        } catch (DataAccessException e) {
            res.status(e.getCode());
            res.body(e.toString());
            JsonObject error = new JsonObject();
            error.addProperty("message", e.getMessage());
            return error;
        }
    }

    private Object logoutEndpoint(Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            if (authToken == null) throw new DataAccessException("Missing Auth Token", 400);
            userService.logout(authToken);
            res.status(200);
            return new JsonObject();
        } catch (DataAccessException e) {
            return handleError(res, e);
        }
    }

    private Object listGamesEndpoint(Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            if (authToken == null) throw new DataAccessException("Missing Auth Token", 400);
            HashSet<GameData> gameList = gameService.listGames(authToken);
            GameList gameListRecord = new GameList(gameList);
            res.status(200);
            return gson.toJson(gameListRecord);
        } catch (DataAccessException e) {
            return handleError(res, e);
        }
    }

    private Object createGameEndpoint(Request req, Response res) {
        String authToken = req.headers("Authorization");
        GameName gameName = gson.fromJson(req.body(), GameName.class);
        try {
            if (authToken == null) throw new DataAccessException("Missing Auth Token", 400);
            if (gameName == null) throw new DataAccessException("Missing Game Name", 400);
            int gameID = gameService.createGame(gameName, authToken);
            res.status(200);
            JsonObject json = new JsonObject();
            json.addProperty("gameID", gameID);
            return json;
        } catch (DataAccessException e) {
            return handleError(res, e);
        }
    }

    private Object joinGameEndpoint(Request req, Response res) {
        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
        String authToken = req.headers("Authorization");

        try {
            if (authToken == null) throw new DataAccessException("Missing Auth Token", 400);
            AuthData userData = authDAO.readAuth(authToken);
            gameService.joinGame(userData.username(), request.playerColor(), request.gameID());
            res.status(200);
        } catch (DataAccessException e) {
            res.status(e.getCode());
            if (res.status() == 200) {
                return new JsonObject();
            }
            res.body(e.toString());
            JsonObject error = new JsonObject();
            error.addProperty("message", "Error: " + e.getMessage());
            return error;
        }

        return new JsonObject();

    }

    private Object clearDBEndpoint(Request req, Response res) throws DataAccessException {
        gameService.clearGames();
        userService.clearUsers();
        authDAO.clearAuth();
        res.status(200);
        return new JsonObject();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(500);
        System.out.println("Exception handler");
    }

    private static JsonObject handleError(Response res, DataAccessException e) {
        res.status(e.getCode());
        res.body(e.toString());
        JsonObject error = new JsonObject();
        error.addProperty("message", "Error: " + e.getMessage());
        return error;
    }
}
