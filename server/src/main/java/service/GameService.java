package service;

import chess.ChessGame;
import com.google.gson.JsonObject;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.GameName;
import model.UserData;

import java.util.*;

public class GameService {

    AuthDAO authDAO;
    GameDAO gameDAO = new GameDAO();

    public GameService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws DataAccessException {
        if (authDAO.readAuth(authToken) == null) throw new DataAccessException("Invalid Auth Token", 401);
        HashSet<GameData> games = gameDAO.listGames();
        return games;
    }
    public void clearGames() throws DataAccessException {
        this.gameDAO.clearGames();
    }
    public int createGame(GameName gameName, String authToken) throws DataAccessException {
        if (null == authDAO.readAuth(authToken)) throw new DataAccessException("Invalid Auth Token", 401);
        int gameID = gameDAO.createGame(gameName.gameName());
        return gameID;
    }

    public void joinGame(String username, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) throw new DataAccessException("Game does not Exist", 400);
        if (color == null) throw new DataAccessException("No color specified", 200);
        switch (color) {
            case WHITE:
                if (null != game.whiteUsername()) throw new DataAccessException("Color already taken.", 403);
                gameDAO.updateGame(game.setPlayer(username, ChessGame.TeamColor.WHITE));
                break;
            case BLACK:
                if (null != game.blackUsername()) throw new DataAccessException("Color already taken.", 403);
                gameDAO.updateGame(game.setPlayer(username, ChessGame.TeamColor.BLACK));
                break;
        }
    }
}
