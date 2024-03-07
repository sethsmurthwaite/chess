package dataAccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DBGameDAO implements GameDAO {
    Gson gson = new Gson();
    DatabaseManager dbman;
    public DBGameDAO(DatabaseManager dbman) {
        this.dbman = dbman;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = -1;
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String whiteUsername = null;
        String blackUsername = null;
        ChessGame chessGame = new ChessGame();
        try {
            gameID = dbman.executeUpdate(statement, whiteUsername, blackUsername, gameName, gson.toJson(chessGame));
        } catch (DataAccessException e) {
            System.out.println("Error in createUser in DBGameDAO: " + e.getMessage());
        }
        return gameID;
    }

    @Override
    public void joinGame(GameData game, String username, ChessGame.TeamColor color) throws DataAccessException {

    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> gameList = new HashSet<>();
        var statement = "select * from game";
        try {
            List<Map<String,Object>> list = dbman.executeQuery(statement);
            parseGameResponse(gameList, list);
        } catch (DataAccessException e) {
            System.out.println("Error in listGames in DBGameDAO: " + e.getMessage());
        }
        return gameList;
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        try {
            dbman.executeUpdate(statement);
        } catch (DataAccessException e) {
            System.out.println("ERROR IN clearAuth IN DBGameDAO " + e);
        }
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        HashSet<GameData> gameList = new HashSet<>();
        GameData returnedGame = null;
        var statement = "SELECT * FROM game WHERE id = ?";
        try {
            List<Map<String, Object>> response = dbman.executeQuery(statement, gameId);
            if (response.isEmpty()) throw new DataAccessException("Invalid Game ID", 400);
            parseGameResponse(gameList, response);
            for (GameData g : gameList) {
                returnedGame = g;
            }
        } catch (DataAccessException e) {
            System.out.println("Error in getGame in DBGameUser: " + e.getMessage());
            throw new DataAccessException("Invalid Game ID", 400);
        }
        return returnedGame;
    }

    private void parseGameResponse(HashSet<GameData> gameList, List<Map<String, Object>> response) {
        for (Map<String, Object> row : response) {
            int gameID = (int) row.get("id");
            String whiteUsername = (String) row.get("whiteUsername");
            String blackUsername = (String) row.get("blackUsername");
            String gameName = (String) row.get("gameName");
            String chessString = (String) row.get("game");
            JsonObject chessJson = gson.fromJson(chessString, JsonObject.class);
            ChessGame chessGame = gson.fromJson(chessJson, ChessGame.class);
            gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
        }
    }

    @Override
    public void updateGame(GameData newGame) {
        var statement = """
                INSERT INTO game (id, whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    whiteUsername = VALUES(whiteUsername),
                    blackUsername = VALUES(blackUsername),
                    gameName = VALUES(gameName),
                    game = VALUES(game);
                """;
        String whiteUsername = newGame.whiteUsername();
        String blackUsername = newGame.blackUsername();
        String gameName = newGame.gameName();
        ChessGame game = newGame.game();
        int id = newGame.gameID();
        try {
            dbman.executeUpdate(statement, id, whiteUsername, blackUsername, gameName, gson.toJson(game));
        } catch (DataAccessException e) {
            System.out.println("not working");
        }
    }


}
