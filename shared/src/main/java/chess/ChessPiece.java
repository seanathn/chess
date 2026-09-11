package chess;

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
    private ChessPiece.PieceType piecce;
    private ChessGame.TeamColor team;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.piecce = type;
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
        return piecce;
    }

    private void rookMoves(ChessBoard board, ChessPosition piece, ArrayList<ChessMove> posMoves) {
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
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
            rookMoves(board, myPosition, moves);
        }


        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return piecce == that.piecce && team == that.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piecce, team);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "piecce=" + piecce +
                ", team=" + team +
                '}';
    }
}
