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
            case KING -> kingRules(board, myPosition);
            case QUEEN -> queenRules(board, myPosition);
            case BISHOP -> bishopRules(board, myPosition);
            case ROOK -> rookRules(board, myPosition);
            case KNIGHT -> knightRules(board, myPosition);
            case PAWN -> pawnRules(board, myPosition, piece);
        };
    }

    private boolean inBounds(int x, int y) {
        return x > 0 && x <= 8 && y > 0 && y <= 8;
    }

    private boolean isEmpty(int x, int y, ChessBoard board) {
        return board.getPiece(new ChessPosition(x, y)) == null;
    }

    private boolean isSameTeam(ChessBoard board, ChessPosition myPosition, int x, int y) {
        if (board.getPiece(new ChessPosition(x, y)) != null) {  // safety in case getPiece returns null
            return board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn())).getTeamColor() != board.getPiece(new ChessPosition(x, y)).getTeamColor();
        }
        return true;
    }


    private ArrayList<ChessMove> getSlidingMoves(int x, int y, int[][] directions, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        for (int[] dir : directions) {
            // sets up the piece to move in the correct direction
            int dx = dir[0];
            int dy = dir[1];

            // initial location once the first directional change
            int nx = x + dx;
            int ny = y + dy;

            while (inBounds(nx, ny)) {
                if (isEmpty(nx, ny, board)) {
                    possibleMoves.add(new ChessMove(new ChessPosition(x, y), new ChessPosition(nx, ny), null));
                } else if (board.getPiece(new ChessPosition(x, y)).getTeamColor() != board.getPiece(new ChessPosition(nx, ny)).getTeamColor()) {
                    possibleMoves.add(new ChessMove(new ChessPosition(x, y), new ChessPosition(nx, ny), null));
                    break;
                } else {
                    break;
                }

                // changes the location but still following the direction until the end or a block
                nx += dx;
                ny += dy;
            }
        }

        return possibleMoves;
    }

    private Collection<ChessMove> bishopRules(ChessBoard board, ChessPosition myPosition) {
        // directions a piece can move in, does allow the continuation of the direction
        int[][] direction = new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        return getSlidingMoves(myPosition.getRow(), myPosition.getColumn(), direction, board);
    }

    private Collection<ChessMove> rookRules(ChessBoard board, ChessPosition myPosition) {
        int[][] direction = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return getSlidingMoves(myPosition.getRow(), myPosition.getColumn(), direction, board);
    }

    private Collection<ChessMove> kingRules(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        return getChessMoves(board, myPosition, possibleMoves, directions);
    }

    private Collection<ChessMove> queenRules(ChessBoard board, ChessPosition myPosition) {
        int[][] direction = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        return getSlidingMoves(myPosition.getRow(), myPosition.getColumn(), direction, board);
    }

    private Collection<ChessMove> knightRules(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] offsets = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        return getChessMoves(board, myPosition, possibleMoves, offsets);
    }

    private Collection<ChessMove> getChessMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> possibleMoves, int[][] offsets) {
        for (int[] offset : offsets) {
            int nx = myPosition.getRow() + offset[0];
            int ny = myPosition.getColumn() + offset[1];
            if (inBounds(nx, ny) && isSameTeam(board, myPosition, nx, ny)) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nx, ny), null));
            }
        }
        return possibleMoves;
    }

    private ChessMove pawnPromotion(ChessPosition myPosition, int direction, PieceType pieceType, int y) {
        return new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + direction, y), pieceType);
    }

    private void pawnCapture(ChessBoard board, ChessPosition myPosition, int direction, int endRow, ArrayList<ChessMove> possibleMoves) {
        for (int dy = -1; dy <= 1; dy += 2) {
            int nx = myPosition.getRow() + direction;
            int ny = myPosition.getColumn() + dy;
            if (inBounds(nx, ny) && !isEmpty(nx, ny, board) && isSameTeam(board, myPosition, nx, ny)) {

                if (myPosition.getRow() == endRow) {
                    PieceType[] promotionOptions = {PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
                    for (PieceType promotion : promotionOptions) {
                            possibleMoves.add(pawnPromotion(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), direction, promotion, ny));
                    }

                } else {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nx, ny), null));
                }
            }
        }
    }

    private Collection<ChessMove> pawnRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int direction = piece.getTeamColor().equals(ChessGame.TeamColor.BLACK) ? -1 : 1;
        int startRow = piece.getTeamColor().equals(ChessGame.TeamColor.BLACK) ? 7 : 2;
        int endRow = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 7 : 2;

        if (isEmpty(myPosition.getRow() + direction, myPosition.getColumn(), board)) {

            if (myPosition.getRow() == endRow) {
                PieceType[] promotionOptions = {PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
                for (PieceType promotion : promotionOptions) {
                        possibleMoves.add(pawnPromotion(myPosition, direction, promotion, myPosition.getColumn()));
                }

            } else {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()), null));
                if (myPosition.getRow() == startRow && isEmpty(myPosition.getRow() + 2 * direction, myPosition.getColumn(), board)) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn()), null));
                }
            }

        }

        pawnCapture(board, myPosition, direction, endRow, possibleMoves);


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
