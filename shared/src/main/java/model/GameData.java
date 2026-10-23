package model;

import chess.ChessGame;

public record GameData(int gameID, String gameName, String whiteUsername, String blackUsername,  ChessGame game) {

    public GameData setWhiteTeam(String username) {
        return new GameData(gameID, gameName, username, blackUsername, game);
    }

    public GameData setBlackTeam(String username) {
        return new GameData(gameID, gameName, whiteUsername, username, game);
    }
}
