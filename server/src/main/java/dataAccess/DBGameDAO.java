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
            for (Map<String,Object> row : list) {
                int gameID = (int) row.get("id");
                String whiteUsername = (String) row.get("whiteUsername");
                String blackUsername = (String) row.get("blackUsername");
                String gameName = (String) row.get("gameName");
                String chessString = (String) row.get("game");
                JsonObject chessJson = gson.fromJson(chessString, JsonObject.class);
                ChessGame chessGame = gson.fromJson(chessJson, ChessGame.class);
                gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
            }
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
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData newGame) {

    }


}
