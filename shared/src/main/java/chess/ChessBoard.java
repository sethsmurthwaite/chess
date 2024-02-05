package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] chessBoard = new ChessPiece[8][8];

    public ChessBoard() {

    }

    public ChessBoard(ChessBoard duplicate) {
        this.chessBoard = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece originalPiece = duplicate.chessBoard[row][col];
                if (originalPiece != null) {
                    ChessPiece copiedPiece = new ChessPiece(originalPiece.getTeamColor(), originalPiece.getPieceType());
                    this.chessBoard[row][col] = copiedPiece;
                } else {
                    this.chessBoard[row][col] = null;
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.chessBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.chessBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        setUpTeam(ChessGame.TeamColor.WHITE);
        setUpTeam(ChessGame.TeamColor.BLACK);
    }

    private void setUpTeam(ChessGame.TeamColor color) {
        int setupRow = (color == ChessGame.TeamColor.WHITE) ? 0 : 7;
        int pawnRow = (color == ChessGame.TeamColor.WHITE) ? 1 : 6;
        chessBoard[setupRow][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        chessBoard[setupRow][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        chessBoard[setupRow][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        chessBoard[setupRow][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        chessBoard[setupRow][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        chessBoard[setupRow][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        chessBoard[setupRow][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        chessBoard[setupRow][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            chessBoard[pawnRow][i] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }
    }

    public ChessPosition getKing(ChessGame.TeamColor color) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == color) {
                    return position;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "chessBoard=" + Arrays.toString(chessBoard) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }
}
