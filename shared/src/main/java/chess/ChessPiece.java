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
    private boolean upRightBlock = false;
    private boolean upLeftBlock = false;

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

        switch (piece.getPieceType()) {
            case KING:
                return kingRules(board, myPosition, piece);
            case QUEEN:
                return queenRules(board, myPosition, piece);
            case BISHOP:
                return bishopRules(board, myPosition, piece);
            case ROOK:
                return rookRules(board, myPosition, piece);
            case KNIGHT:
                return knightRules(board, myPosition, piece);
            case PAWN:
                return pawnRules(board, myPosition, piece);
        }


        if (piece.getPieceType() == PieceType.BISHOP) {
            return bishopRules(board, myPosition, piece);
        } else if (piece.getPieceType() == PieceType.PAWN) {
            return pawnRules(board, myPosition, piece);
        } else


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

    private Collection<ChessMove> bishopRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();


        for (int x = 1; x <= 8; x++) {
            if (upLeftBlock && x < myPosition.getRow()) {
                x = myPosition.getRow();
            }
            for (int y = 1; y <= 8; y++) {
                if (upLeftBlock && y < myPosition.getColumn()) {
                    y = myPosition.getColumn();
                }
                if (upRightBlock && y > myPosition.getColumn()) {
                    break;
                }
                   possibleMoves = addDiag(board, myPosition, x, y, possibleMoves, piece);
                }
            }
        return possibleMoves;
    }

    private boolean isDiag(int x, int y, ChessPosition myPosition) {
        int posX = myPosition.getRow();
        int posY = myPosition.getColumn();
        if (x == posX && y == posY) {
            return false;
        }
        return Math.abs(posX - x) == Math.abs(posY - y);
    }

    private ArrayList<ChessMove> addDiag(ChessBoard board, ChessPosition myPosition, int x, int y, ArrayList<ChessMove> possibleMoves, ChessPiece piece) {
        if (isDiag(x, y, myPosition)) {
            if (board.getPiece(new ChessPosition(x, y)) == null) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
            } else {
                if (x < myPosition.getRow() && y < myPosition.getColumn()) {
                    Iterator<ChessMove> iter = possibleMoves.iterator();

                    while (iter.hasNext()) {
                        ChessMove move = iter.next();

                        if (move.getEndPosition().getRow() < x && move.getEndPosition().getColumn() < y) {
                            iter.remove();
                        }
                    }
                } else if (x > myPosition.getRow() && y > myPosition.getColumn()) {
                    upRightBlock = true;
                } else if (x < myPosition.getRow() && y > myPosition.getColumn()) {
                    Iterator<ChessMove> iter = possibleMoves.iterator();

                    while (iter.hasNext()) {
                        ChessMove move = iter.next();

                        if (move.getEndPosition().getRow() < x && move.getEndPosition().getColumn() > y) {
                            iter.remove();
                        }
                    }
                } else if (x > myPosition.getRow() && y < myPosition.getColumn()) {
                    upLeftBlock = true;
                }
                if (board.getPiece(new ChessPosition(x, y)).getTeamColor() != piece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                }
            }
        }
        return possibleMoves;
    }


    private Collection<ChessMove> rookRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                if (x != myPosition.getRow() || y != myPosition.getColumn()) {
                    if (x == myPosition.getRow()) {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    }
                    if (y == myPosition.getColumn()) {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    }
                }
            }
        }

        return possibleMoves;
    }


    private Collection<ChessMove> kingRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();


        return possibleMoves;
    }


    private Collection<ChessMove> queenRules(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();


        return possibleMoves;
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
