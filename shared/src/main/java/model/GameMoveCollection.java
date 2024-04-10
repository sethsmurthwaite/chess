package model;

import chess.ChessMove;

import java.util.Collection;
import java.util.HashSet;

public class GameMoveCollection {
    HashSet<ChessMove> validMoves;
    public GameMoveCollection() {
        validMoves = new HashSet<ChessMove>();
    }

    public void addToCollection(ChessMove m) {
        validMoves.add(m);
    }

    public HashSet<ChessMove> getValidMoves() {
        return validMoves;
    }

}
