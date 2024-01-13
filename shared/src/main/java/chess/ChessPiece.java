package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        PieceType type  = piece.getPieceType();

        return switch (type) {
            case KING -> kingMoves(board, myPosition, color);
            case QUEEN -> queenMoves(board, myPosition, color);
            case BISHOP -> bishopMoves(board, myPosition, color);
            case KNIGHT -> knightMoves(board, myPosition, color);
            case ROOK -> rookMoves(board, myPosition, color);
            case PAWN -> pawnMoves(board, myPosition, color);
        };
    }


    private HashSet<ChessMove> kingMoves(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor kingColor) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        int kingRow = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                ChessPosition targetPosition =
                        new ChessPosition(kingRow - rowOffset, kingCol - colOffset);

                // Check if targetPosition is out of bounds
                if (targetPosition.isOutOfBounds()) continue;

                ChessPiece occupyingPiece = board.getPiece(targetPosition);

                // Checks if the space is empty
                // Also checks if piece occupying space is can be captured
                if (occupyingPiece == null || occupyingPiece.getTeamColor() != kingColor) {
                    possibleMoves.add(new ChessMove(kingPosition, targetPosition));
                }
            }
        }

        return possibleMoves;
    }

    private HashSet<ChessMove> queenMoves(ChessBoard board, ChessPosition queenPosition, ChessGame.TeamColor queenColor) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        int queenRow = queenPosition.getRow();
        int queenCol = queenPosition.getColumn();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                movesInStraightLine(board, queenPosition, queenColor, possibleMoves, queenRow, queenCol, rowOffset, colOffset);
            }
        }
        return possibleMoves;
    }

    private HashSet<ChessMove> bishopMoves(ChessBoard board, ChessPosition bishopPosition, ChessGame.TeamColor bishopColor) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int bishopRow = bishopPosition.getRow();
        int bishopCol = bishopPosition.getColumn();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset += 2) {
            for (int colOffset = -1; colOffset <= 1; colOffset += 2) {
                movesInStraightLine(board, bishopPosition, bishopColor, possibleMoves, bishopRow, bishopCol, rowOffset, colOffset);
            }
        }
        return possibleMoves;
    }

    private HashSet<ChessMove> knightMoves(ChessBoard board, ChessPosition knightPosition, ChessGame.TeamColor knightColor) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int knightRow = knightPosition.getRow();
        int knightCol = knightPosition.getColumn();

        int[] rowDirections = {-2, -2, 2, 2, -1, -1, 1, 1};
        int[] colDirections = {-1, 1, -1, 1, -2, 2, -2, 2};

        for (int i = 0; i < rowDirections.length; i++) {
            ChessPosition targetPosition =
                    new ChessPosition(
                            knightRow - rowDirections[i],
                            knightCol - colDirections[i]);

            // Check if targetPosition is out of bounds
            if (targetPosition.isOutOfBounds()) continue;

            ChessPiece occupyingPiece = board.getPiece(targetPosition);

            // Checks if the space is empty
            // Also checks if piece occupying space is can be captured
            if (occupyingPiece == null || occupyingPiece.getTeamColor() != knightColor) {
                possibleMoves.add(new ChessMove(knightPosition, targetPosition));
            }

        }

        return possibleMoves;
    }

    private HashSet<ChessMove> rookMoves(ChessBoard board, ChessPosition rookPosition, ChessGame.TeamColor rookColor) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int rookRow = rookPosition.getRow();
        int rookCol = rookPosition.getColumn();

        int[] rowDirections = {-1, 0, 0, 1};
        int[] colDirections = {0, -1, 1, 0};

        for (int i = 0; i < 4; i++) {
            movesInStraightLine(board, rookPosition, rookColor, possibleMoves,
                    rookRow, rookCol, rowDirections[i], colDirections[i]);
        }

        return possibleMoves;
    }

    private HashSet<ChessMove> pawnMoves(ChessBoard board, ChessPosition pawnPosition, ChessGame.TeamColor pawnColor) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();

        int pawnRow = pawnPosition.getRow();
        int pawnCol = pawnPosition.getColumn();
        int[] rowDirections;
        int[] colDirections;

        if (pawnColor == ChessGame.TeamColor.WHITE) {
            if (pawnRow == 2) {
                rowDirections = new int[]{1, 1, 1, 2};
                colDirections = new int[]{0, -1, 1, 0};
            } else {
                rowDirections = new int[]{1, 1, 1};
                colDirections = new int[]{0, -1, 1};
            }
        }
        else {
            if (pawnRow == 7) {
                rowDirections = new int[]{-1, -1, -1, -2};
                colDirections = new int[]{0, -1, 1, 0};
            } else {
                rowDirections = new int[]{-1, -1, -1};
                colDirections = new int[]{0, -1, 1};
            }
        }

        for (int i = 0; i < rowDirections.length; i++) {
            ChessPosition targetPosition = new ChessPosition(
                    pawnRow + rowDirections[i],
                    pawnCol + colDirections[i]);

            if (targetPosition.isOutOfBounds()) continue;
            ChessPiece occupyingPiece = board.getPiece(targetPosition);

            if (occupyingPiece == null) {
                if (colDirections[i] != 0) continue;
                if (rowDirections[i]== 2) {
                    if (board.getPiece(new ChessPosition(
                            pawnRow + rowDirections[i - 1],
                            pawnCol + colDirections[i])) != null) continue;
                }
                if (rowDirections[i] == -2) {
                    if (board.getPiece(new ChessPosition(
                            pawnRow + rowDirections[i - 1],
                            pawnCol + colDirections[i])) != null) continue;
                }

                if (targetPosition.getRow() == 1 || targetPosition.getRow() == 8) {
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.BISHOP));
                } else {
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition));
                }
            }
            else if (pawnColor != occupyingPiece.getTeamColor()) {
                if (colDirections[i] == 0) continue;
                if (targetPosition.getRow() == 1 || targetPosition.getRow() == 8) {
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition, PieceType.BISHOP));
                } else {
                    possibleMoves.add(new ChessMove(pawnPosition, targetPosition));
                }
            }
        }

        return possibleMoves;
    }

    private void movesInStraightLine(ChessBoard board, ChessPosition position, ChessGame.TeamColor color,
                                     HashSet<ChessMove> possibleMoves, int row, int col, int rowOffset, int colOffset) {
        for (int distance = 1; distance < 8; distance++) {

            ChessPosition targetPosition = new ChessPosition(
                    row - rowOffset * distance,
                    col - colOffset * distance);

            if (targetPosition.isOutOfBounds()) continue;
            ChessPiece occupyingPiece = board.getPiece(targetPosition);

            if (occupyingPiece == null) {
                possibleMoves.add(new ChessMove(position, targetPosition));
            } else if (occupyingPiece.getTeamColor() != color) {
                possibleMoves.add(new ChessMove(position, targetPosition));
                break;
            } else break;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
