package dataAccess;

import model.GameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.HashSet;


public class GameDAO {

    HashMap<Integer, GameData> gameTable = new HashMap<Integer, GameData>();

    public int createGame(String gameName) throws DataAccessException {
        int gameID = gameTable.size() + 1;
        gameTable.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }
    public void joinGame(GameData game, String username, ChessGame.TeamColor color) throws DataAccessException {

    }
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> gameList = new HashSet<>();
        for (GameData game : gameTable.values()) {
            gameList.add(game);
        }
        return gameList;
    }
    public void clearGames() throws DataAccessException {
        this.gameTable.clear();
    }


    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = gameTable.get(gameID);
        if (game == null) throw new DataAccessException("Invalid Game ID", 400);
        return game;
    }

    public void updateGame(GameData newGame) {
        gameTable.put(newGame.gameID(), newGame);
    }
}
