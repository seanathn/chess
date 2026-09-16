package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessPiece.PieceType piece;
    private final ChessGame.TeamColor team;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.piece = type;
        this.team = pieceColor;
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
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return piece;
    }

    private void slideMoves(ChessBoard board, ChessPosition piece, ArrayList<ChessMove> posMoves, int[][] dirs) {
        for (int[] dir : dirs) {
            int row = piece.getRow();
            int col = piece.getColumn();
            row += dir[0];
            col += dir[1];
            while(row < 9 && row > 0 && col < 9 && col > 0) {
                ChessPosition cur = new ChessPosition(row, col);
                ChessMove move = new ChessMove(piece, cur, null);
                if (board.getPiece(cur) == null) {
                    posMoves.add(move);
                } else if (board.getPiece(cur).getTeamColor() != board.getPiece(piece).getTeamColor()) {
                    posMoves.add(move);
                    break;
                } else {
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
    }

    private void placeMoves(ChessBoard board, ChessPosition piece, ArrayList<ChessMove> posMoves, int[][] dirs) {
        for (int[] dir :dirs) {
            int row = piece.getRow() + dir[0];
            int col = piece.getColumn() + dir[1];
            if (row < 9 && row > 0 && col > 0 && col < 9) {
                ChessPosition cur = new ChessPosition(row, col);
                ChessMove move = new ChessMove(piece, cur, null);
                if (board.getPiece(cur) == null) {
                    posMoves.add(move);
                } else if (board.getPiece(cur).getTeamColor() != board.getPiece(piece).getTeamColor()) {
                    posMoves.add(move);
                }
            }
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece chessPiece = board.getPiece(myPosition);
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (chessPiece.getPieceType() == PieceType.ROOK) {
            int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
            slideMoves(board, myPosition, moves, dirs);
        } else if (chessPiece.getPieceType() == PieceType.BISHOP) {
            int[][] dirs = {{1,1}, {-1,-1}, {-1,1}, {1,-1}};
            slideMoves(board, myPosition, moves, dirs);
        } else if (chessPiece.getPieceType() == PieceType.QUEEN) {
            int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,-1}, {-1,1}, {1,-1}};
            slideMoves(board, myPosition, moves, dirs);
        } else if (chessPiece.getPieceType() == PieceType.KING) {
            int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,-1}, {-1,1}, {1,-1}};
            placeMoves(board, myPosition, moves, dirs);
        }


        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return piece == that.piece && team == that.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, team);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "piece=" + piece +
                ", team=" + team +
                '}';
    }
}
