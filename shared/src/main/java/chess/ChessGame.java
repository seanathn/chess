package chess;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard curBoard;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        curBoard = new ChessBoard();
        curBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece curPiece = curBoard.getPiece(startPosition);
        Collection<ChessMove> posMoves = curPiece.pieceMoves(curBoard, startPosition);
        for (ChessMove move : posMoves) {
            ChessBoard temp = curBoard;
            temp.addPiece(move.getEndPosition(), temp.getPiece(move.getStartPosition()));
            temp.addPiece(move.getStartPosition(), null);
            if (isInCheck(curPiece.getTeamColor())) {
                posMoves.remove(move);
            }
        }
        return posMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves.contains(move) && teamTurn == curBoard.getPiece(move.getStartPosition()).getTeamColor()) {
            curBoard.addPiece(move.getEndPosition(), curBoard.getPiece(move.getStartPosition()));
            curBoard.addPiece(move.getStartPosition(), null);
            // may need to have teamColor switch once move is done.
        } else {
            throw new InvalidMoveException("Move not valid");
        }
    }

    private ArrayList<ChessPosition> getTeamPiecePositions(TeamColor teamColor) {
        ArrayList<ChessPosition> teamPos = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (curBoard.getPiece(new ChessPosition(i, j)) != null
                        && curBoard.getPiece(new ChessPosition(i, j)).getTeamColor() != teamColor) {
                    teamPos.add(new ChessPosition(i, j));
                }
            }
        }
        return teamPos;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ArrayList<ChessPosition> opPiecePos = getTeamPiecePositions(teamColor);
        Set<ChessMove> opMoves = new HashSet<>();
        ChessPosition kingPos = null;
        for (ChessPosition curPos : opPiecePos) {
            opMoves.addAll(curBoard.getPiece(curPos).pieceMoves(curBoard, curPos));
        }
        for (ChessMove curMove : opMoves) {
            if (curMove.getEndPosition() == kingPos) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            // king is in check and team no valid moves
            throw new RuntimeException("Not implemented");
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            // team has no valid moves
            throw new RuntimeException("Not implemented");
        }
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        curBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return curBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(curBoard, chessGame.curBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, curBoard);
    }
}
