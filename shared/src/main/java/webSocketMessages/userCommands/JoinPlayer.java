package webSocketMessages.userCommands;

import chess.ChessGame;
import model.GameData;

public class JoinPlayer extends UserGameCommand {

    ChessGame.TeamColor playerColor;
    int gameID;

    public JoinPlayer(String authToken, ChessGame.TeamColor color, int gameID) {
        super(authToken, UserGameCommand.CommandType.JOIN_PLAYER);
        playerColor = color;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getColor() {return playerColor;}
    public Integer getGameID() { return this.gameID; }

}
