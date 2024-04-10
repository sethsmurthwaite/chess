package model;
import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData setPlayer(String userName, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) return new GameData(gameID, userName, blackUsername, gameName, game);
        else return new GameData(gameID, whiteUsername, userName, gameName, game);
    }
    public GameData setGame(ChessGame newGame) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, newGame);
    }
}

