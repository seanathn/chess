package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataacess.*;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ChessService {

    private final DataAccessAuths authAccess = new DataAccessAuths();
    private final DataAccessGames gameAccess = new DataAccessGames();
    private final DataAccessUsers userAccess = new DataAccessUsers();
    private int gameIDNum = 0;

    public String registerRequest(UserData input) throws DataAccessException {
        if (input.username() == null || input.password() == null) {
            throw new BadRequestError();
        }
        UserData user = userAccess.findUserByUsername(input.username());

        if (user == null) {
            userAccess.addUser(input);
//             create authdata record and place in list
            return new Gson().toJson(createAuthData(input.username()));
        } else {
            throw new AlreadyTakenException();
        }
    }

    public void clearRequest() {
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }

    public String loginRequest(UserData input) throws DataAccessException {
        if (input.username() == null || input.password() == null) {
            throw new BadRequestError();
        }
        UserData user = userAccess.findUserByUsername(input.username());
        if (user != null && Objects.equals(input.password(), user.password())) {
            return new Gson().toJson(createAuthData(input.username()));
        } else {
            throw new UnauthorizedException();
        }
    }

    public void logoutRequest(String authToken) throws DataAccessException {
        AuthData auth = authAccess.findAuthByAuthToken(authToken);
        if (auth != null) {
            authAccess.removeAuth(auth);
        } else {
            throw new UnauthorizedException();
        }
    }

    public ArrayList<GameData> listGamesRequest(String authToken) throws DataAccessException {
        if (authorized(authToken)) {
            return gameAccess.getListGames();
        }
        return null;
    }

    public int createGameRequest(String authToken, GameData game) throws DataAccessException {
        if (game.gameName() == null) {
            throw new BadRequestError();
        }
        if (authorized(authToken)) {
            gameIDNum++;
            GameData newGame = new GameData(gameIDNum, game.gameName(), null, null, new ChessGame());
            gameAccess.addGame(newGame);
            return newGame.gameID();
        }
        return 0;
    }

    public void joinGameRequest(String authToken, JoinGameRequest request) throws DataAccessException {
        if (authorized(authToken)) {
            GameData game = gameAccess.findGameByID(request.gameID());
            if (game == null) {
                throw new BadRequestError();
            }
            if (checkIfTeamEmpty(request.playerColor(), game)) {
                gameAccess.setTeamUser(request.playerColor(), authAccess.
                        findAuthByAuthToken(authToken).username(), game);
            } else {
                throw new AlreadyTakenException();
            }
        }
    }

    private boolean checkIfTeamEmpty(String teamColor, GameData game) throws DataAccessException {
        if (teamColor == null) {
            throw new BadRequestError();
        }
        if (Objects.equals(teamColor, "WHITE")) {
            return game.whiteUsername() == null;
        } else if (teamColor.equals("BLACK")) {
            return game.blackUsername() == null;
        }
        throw new BadRequestError();
    }

    private AuthData createAuthData(String username) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authAccess.addAuth(auth);
        return auth;
    }

    private boolean authorized(String authToken) throws DataAccessException {
        if (authAccess.findAuthByAuthToken(authToken) == null) {
            throw new UnauthorizedException();
        }
        return true;
    }
}
