package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.GameName;

import java.util.*;

public class GameService {

    AuthDAO authDAO;
    GameDAO gameDAO = new GameDAO();

    public GameService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws DataAccessException {
        authDAO.readAuth(authToken);
        return gameDAO.listGames();
    }
    public void clearGames() throws DataAccessException {
        this.gameDAO.clearGames();
    }
    public int createGame(GameName gameName, String authToken) throws DataAccessException {
        authDAO.readAuth(authToken);
        return gameDAO.createGame(gameName.gameName());
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
