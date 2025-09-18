package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
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
        if (piece.getPieceType() == PieceType.BISHOP) {

        } else if (piece.getPieceType() == PieceType.PAWN) {
            return pawnRules(board, myPosition, piece);
        }


        return List.of();
    }

    private Collection<ChessMove> pawnRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int i;
        // offset for row depending on team color
        if (piece.pieceColor == ChessGame.TeamColor.BLACK) {
            i = -1;
        } else {
            i = 1;
        }

        // Check if space in front is empty
        if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn())).getPieceType() == null) {

        } else if(board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - 1)).getPieceType() != null) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - 1)).getTeamColor() != piece.getTeamColor()) {


                // can capture to the left
            }

        } else if(board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + 1)).getPieceType() != null) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + 1)).getTeamColor() != piece.getTeamColor()) {


                //can capture to the right
            }
        }



//        if (myPosition.getRow() == 2 && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            // Check if path is clear
//            if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
//                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
//                if (board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null) {
//                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()), null));
//                }
//            }
//        } else if (myPosition.getRow() == 7 && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
//            // Check if path is clear
//            if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
//                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
//                if (board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null) {
//                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()), null));
//                }
//            }
//        }

        return possibleMoves;
    }

    private void queenRules(ChessBoard board, ChessPosition myPosition) {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
