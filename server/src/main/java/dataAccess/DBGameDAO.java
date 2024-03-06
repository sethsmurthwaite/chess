package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class DBGameDAO implements GameDAO {

    DatabaseManager dbman;
    public DBGameDAO(DatabaseManager dbman) {
        this.dbman = dbman;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void joinGame(GameData game, String username, ChessGame.TeamColor color) throws DataAccessException {

    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void clearGames() throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData newGame) {

    }


}
