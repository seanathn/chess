package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] boardArray;

    public ChessBoard() {
        boardArray = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int actualPositionRow = 8 - position.getRow();
        boardArray[actualPositionRow][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardArray[8-position.getRow()][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        addPiece(new ChessPosition(8,1), blackRook);
        addPiece(new ChessPosition(8,8), blackRook);
        addPiece(new ChessPosition(8,2), blackKnight);
        addPiece(new ChessPosition(8,7), blackKnight);
        addPiece(new ChessPosition(8,3), blackBishop);
        addPiece(new ChessPosition(8,6), blackBishop);
        addPiece(new ChessPosition(8,4), blackQueen);
        addPiece(new ChessPosition(8,5), blackKing);
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7,i), blackPawn);
        }

        addPiece(new ChessPosition(1,1), whiteRook);
        addPiece(new ChessPosition(1,8), whiteRook);
        addPiece(new ChessPosition(1,2), whiteKnight);
        addPiece(new ChessPosition(1,7), whiteKnight);
        addPiece(new ChessPosition(1,3), whiteBishop);
        addPiece(new ChessPosition(1,6), whiteBishop);
        addPiece(new ChessPosition(1,4), whiteQueen);
        addPiece(new ChessPosition(1,5), whiteKing);
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2,i), whitePawn);
        }
    }

    public ChessBoard copyBoard() {
        ChessBoard board = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                board.addPiece(new ChessPosition(i,j), boardArray[8-i][j-1]);
            }
        }
        return board;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(boardArray, that.boardArray);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardArray);
    }
}
