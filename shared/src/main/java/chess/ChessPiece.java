package chess;

import java.util.*;

import static java.lang.Math.abs;

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

        return switch (piece.getPieceType()) {
            case KING -> kingRules(board, myPosition, piece);
            case QUEEN -> queenRules(board, myPosition);
            case BISHOP -> bishopRules(board, myPosition);
            case ROOK -> rookRules(board, myPosition);
            case KNIGHT -> knightRules(board, myPosition, piece);
            case PAWN -> pawnRules(board, myPosition, piece);
        };
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
        if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn())) == null) {

        } else if(board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - 1)) != null) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - 1)).getTeamColor() != piece.getTeamColor()) {


                // can capture to the left
            }

        } else if(board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + 1)) != null) {
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

    private Collection<ChessMove> bishopRules(ChessBoard board, ChessPosition myPosition) {
        int [][] direction = new int[][] {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
        return getSlidingMoves(myPosition.getRow(), myPosition.getColumn(), direction, board);
    }

    private boolean inBounds(int x, int y) {
        return x > 0 && x <= 8 && y > 0 && y <= 8;
    }

    private boolean isEmpty(int x, int y, ChessBoard board) {
        return board.getPiece(new ChessPosition(x, y)) == null;
    }


    private ArrayList<ChessMove> getSlidingMoves(int x, int y, int[][] directions, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            int nx = x + dx, ny = y + dy;

            while (inBounds(nx, ny)) {
                if (isEmpty(nx, ny, board)) {
                    possibleMoves.add(new ChessMove(new ChessPosition(x,y), new ChessPosition(nx, ny), null));
                } else if (board.getPiece(new ChessPosition(x,y)).getTeamColor() != board.getPiece(new ChessPosition(nx,ny)).getTeamColor()) {
                    possibleMoves.add(new ChessMove(new ChessPosition(x,y), new ChessPosition(nx, ny), null));
                    break;
                } else {
                    break;
                }
                nx += dx;
                ny += dy;
            }
        }

        return possibleMoves;
    }

    private Collection<ChessMove> rookRules(ChessBoard board, ChessPosition myPosition) {
        int [][] direction = new int[][] {{1,0}, {-1,0}, {0,1}, {0,-1}};
        return getSlidingMoves(myPosition.getRow(), myPosition.getColumn(), direction, board);
    }

    private Collection<ChessMove> kingRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();


        return possibleMoves;
    }

    private Collection<ChessMove> queenRules(ChessBoard board, ChessPosition myPosition) {
        int [][] direction = new int[][] {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
        return getSlidingMoves(myPosition.getRow(), myPosition.getColumn(), direction, board);
    }

    private Collection<ChessMove>  knightRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();


        return possibleMoves;
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
