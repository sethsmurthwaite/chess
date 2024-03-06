package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    public int createGame(String gameName) throws DataAccessException;
    public void joinGame(GameData game, String username, ChessGame.TeamColor color) throws DataAccessException;
    public HashSet<GameData> listGames() throws DataAccessException;
    public void clearGames() throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public void updateGame(GameData newGame);
}
