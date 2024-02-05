package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentTeamTurn = TeamColor.WHITE;
    ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    private void advanceTurn() {
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else { setTeamTurn(TeamColor.WHITE); }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition endPos = move.getEndPosition();
        ChessPosition startPos = move.getStartPosition();
        ChessPiece movingPiece = board.getPiece(startPos);
        ChessPiece occupyingPiece = board.getPiece(endPos);

        if (move.getStartPosition().isOutOfBounds() || move.getEndPosition().isOutOfBounds()) {
            throw new InvalidMoveException("A move was attempted that was out of bounds.");
        }
        if (movingPiece.getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException("Out of turn.");
        }
        if (!movingPiece.pieceMoves(board, startPos).contains(move)) {
            throw new InvalidMoveException("A move was attempted that was not a legal move.");
        }

        board.addPiece(endPos, movingPiece);
        board.addPiece(startPos, null);

        if (isInCheck(getTeamTurn())) {
            board.addPiece(startPos, movingPiece);
            board.addPiece(endPos, occupyingPiece);
            throw new InvalidMoveException("King is in check");
        }

        if (move.getPromotionPiece() != null) {
            ChessPiece.PieceType promoPiece = move.getPromotionPiece();
            TeamColor pieceColor = movingPiece.getTeamColor();
            board.addPiece(endPos, new ChessPiece(pieceColor, promoPiece));
        }

        advanceTurn();

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKing(teamColor);
        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() == teamColor) continue;
                for (ChessMove possibleMove : piece.pieceMoves(board, position)) {
                    if (possibleMove.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        TeamColor enemyColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() != teamColor) continue;
                for (ChessMove move : piece.pieceMoves(board, position)) {
                    ChessPosition endPos = move.getEndPosition();
                    try {
                        makeMove(move);
                    } catch (InvalidMoveException badMove) {
                        System.out.println("You're making a bad move " + badMove);
                    }
                    if (!isInCheck(teamColor)) {
                        return false;
                    }
                }


            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
