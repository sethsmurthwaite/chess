package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    ChessMove move;
    int gameID;


    public MakeMove(String authToken, ChessMove chessMove, int id) {
        super(authToken, CommandType.MAKE_MOVE);
        move = chessMove;
        gameID = id;
    }

    public ChessMove getMove() { return move; }
    public int getGameID() { return gameID; }
}
