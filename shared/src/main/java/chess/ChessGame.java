package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamColor;
    private ChessBoard board;

    public ChessGame() {
        teamColor = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private void movePiece(ChessMove move, ChessBoard board) {
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(teamColor, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        }
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
//        teamColor = board.getPiece(startPosition).getTeamColor();

        if (board.getPiece(startPosition) == null) {
            return null;
        }
        ArrayList<ChessMove> moves = new ArrayList<>(board.getPiece(startPosition).pieceMoves(board, startPosition));
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessMove reverseMove;
            if (move.getPromotionPiece() != null) {
                reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), ChessPiece.PieceType.PAWN);
            } else {
                reverseMove = new ChessMove(move.getEndPosition(), move.getStartPosition(), null);
            }
            ChessPiece tempPiece = null;
            if (board.getPiece(move.getEndPosition()) != null) {
                tempPiece = board.getPiece(move.getEndPosition());
            }
            movePiece(move, board);
            if (!isInCheck(teamColor)) {
                validMoves.add(move);
            }
            movePiece(reverseMove, board);
            if (tempPiece != null) {
                board.addPiece(move.getEndPosition(), tempPiece);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Move not valid");
        }
        if (teamColor != board.getPiece(move.getStartPosition()).getTeamColor()) {
            throw new InvalidMoveException("Move not valid");
        }
        if (validMoves(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Move not valid");
        }
        if (validMoves(move.getStartPosition()).contains(move)) {
            movePiece(move, board);
        } else {
            throw new InvalidMoveException("Move not valid");
        }
        if (teamColor ==  TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition currentPos = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(currentPos);
                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        if (piece.pieceMoves(board, currentPos).contains(new ChessMove(currentPos, kingPos, null)) || piece.pieceMoves(board, currentPos).contains(new ChessMove(currentPos, kingPos, ChessPiece.PieceType.QUEEN))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public ChessPosition findKing(TeamColor teamColor) {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition position = new ChessPosition(x, y);
                if (board.getPiece(position) != null) {
                    if (board.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) {
                        if (teamColor == board.getPiece(position).getTeamColor()) {
                            return position;
                        }
                    }
                }

            }
        }
        // won't be reached
        return null;
    }

    private boolean hasNoValidMove(TeamColor teamColor) {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                if (board.getPiece(new ChessPosition(x, y)) != null) {
                    if  (teamColor == board.getPiece(new ChessPosition(x, y)).getTeamColor()) {
                        if (!validMoves(new ChessPosition(x, y)).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && hasNoValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        } else {
            return hasNoValidMove(teamColor);
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamColor == chessGame.teamColor && board.equals(chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, getBoard());
    }
}
