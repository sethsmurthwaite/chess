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
        return switch (type) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    private HashSet<ChessMove> kingMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();
        for (int rowOffset = -1; rowOffset < 2; rowOffset++) {
            for (int colOffset = -1; colOffset < 2; colOffset++) {
                ChessPosition targetPosition = new ChessPosition(
                        row + rowOffset,
                        col + colOffset);
                if (targetPosition.isOutOfBounds()) continue;
                ChessPiece occupyingPiece = board.getPiece(targetPosition);
                if (occupyingPiece == null) {
                    possibleMoves.add(new ChessMove(position, targetPosition));
                }
                if (occupyingPiece != null && occupyingPiece.pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(position, targetPosition));
                }
            }
        }

        return possibleMoves;
    }

    private HashSet<ChessMove> queenMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();

        for (int rowOffset = -1; rowOffset < 2; rowOffset++) {
            for (int colOffset = -1; colOffset < 2; colOffset++) {
                for (int distance = 1; distance <= 8; distance++) {
                    ChessPosition targetPosition = new ChessPosition(
                            row + rowOffset * distance,
                            col + colOffset * distance);
                    if (targetPosition.isOutOfBounds()) continue;
                    ChessPiece occupyingPiece = board.getPiece(targetPosition);
                    if (occupyingPiece == null) {
                        possibleMoves.add(new ChessMove(position, targetPosition));
                    } else if (occupyingPiece.pieceColor != pieceColor) {
                        possibleMoves.add(new ChessMove(position, targetPosition));
                        break;
                    } else break;
                }
            }
        }

        return possibleMoves;
    }

    private HashSet<ChessMove> bishopMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();

        for (int rowOffset = -1; rowOffset < 2; rowOffset += 2) {
            for (int colOffset = -1; colOffset < 2; colOffset += 2) {
                for (int distance = 1; distance <= 8; distance++) {
                    ChessPosition targetPosition = new ChessPosition(
                            row + rowOffset * distance,
                            col + colOffset * distance);
                    if (targetPosition.isOutOfBounds()) continue;
                    ChessPiece occupyingPiece = board.getPiece(targetPosition);
                    if (occupyingPiece == null) {
                        possibleMoves.add(new ChessMove(position, targetPosition));
                    } else if (occupyingPiece.pieceColor != pieceColor) {
                        possibleMoves.add(new ChessMove(position, targetPosition));
                        break;
                    } else break;
                }
            }
        }

        return possibleMoves;
    }

    private HashSet<ChessMove> knightMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();

        int[] rowMoves = new int[]{2, 2, 1, 1, -1, -1, -2, -2};
        int[] colMoves = new int[]{-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < rowMoves.length; i++) {
            int rowOffset = rowMoves[i];
            int colOffset = colMoves[i];

            ChessPosition targetPosition = new ChessPosition(
                    row + rowOffset,
                    col + colOffset);

            if (targetPosition.isOutOfBounds()) continue;

            ChessPiece occupyingPiece = board.getPiece(targetPosition);

            if (occupyingPiece == null) {
                possibleMoves.add(new ChessMove(position, targetPosition));
            } else if (occupyingPiece.pieceColor != pieceColor) {
                possibleMoves.add(new ChessMove(position, targetPosition));
            }
            ;
        }
        return possibleMoves;
    }

    private HashSet<ChessMove> rookMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();

        for (int rowOffset = -1; rowOffset < 2; rowOffset++) {
            for (int colOffset = -1; colOffset < 2; colOffset++) {
                for (int distance = 1; distance <= 8; distance++) {
                    if (rowOffset != 0 && colOffset != 0) continue;
                    ChessPosition targetPosition = new ChessPosition(
                            row + rowOffset * distance,
                            col + colOffset * distance);
                    if (targetPosition.isOutOfBounds()) continue;
                    ChessPiece occupyingPiece = board.getPiece(targetPosition);
                    if (occupyingPiece == null) {
                        possibleMoves.add(new ChessMove(position, targetPosition));
                    } else if (occupyingPiece.pieceColor != pieceColor) {
                        possibleMoves.add(new ChessMove(position, targetPosition));
                        break;
                    } else break;
                }
            }
        }

        return possibleMoves;
    }

    private HashSet<ChessMove> pawnMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[] rowDirections;
        int[] colDirections;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                rowDirections = new int[]{1, 1, 1, 2};
                colDirections = new int[]{-1, 0, 1, 0};
            } else {
                rowDirections = new int[]{1, 1, 1};
                colDirections = new int[]{-1, 0, 1};
            }
        } else {
            if (row == 7) {
                rowDirections = new int[]{-1, -1, -1, -2};
                colDirections = new int[]{-1, 0, 1, 0};
            } else {
                rowDirections = new int[]{-1, -1, -1};
                colDirections = new int[]{-1, 0, 1};
            }
        }

        for (int i = 0; i < rowDirections.length; i++) {
            int rowOffset = rowDirections[i];
            int colOffset = colDirections[i];
            ChessPosition targetPosition = (new ChessPosition(
                    row + rowOffset,
                    col + colOffset));

            if (targetPosition.isOutOfBounds()) continue;

            ChessPiece occupyingPiece = board.getPiece(targetPosition);

            if (occupyingPiece == null) {

                if (colOffset != 0) continue;

                if (targetPosition.getRow() == 8 || targetPosition.getRow() == 1) {
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.KNIGHT));
                    continue;
                }

                if (rowOffset == 2) {
                    if (board.getPiece(new ChessPosition(
                            row + rowOffset - 1,
                            col + colOffset)) != null) {
                        continue;
                    }
                } else if (rowOffset == -2) {
                    if (board.getPiece(new ChessPosition(
                            row + rowOffset + 1,
                            col + colOffset)) != null) {
                        continue;
                    }
                }

                possibleMoves.add(new ChessMove(position, targetPosition));

            }

            if (occupyingPiece != null && occupyingPiece.getTeamColor() != pieceColor) {
                if (colOffset == 0) continue;

                if (targetPosition.getRow() == 8 || targetPosition.getRow() == 1) {
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(position, targetPosition, PieceType.KNIGHT));
                } else {
                    possibleMoves.add(new ChessMove(position, targetPosition));
                }

            }
        }

        return possibleMoves;
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
}
