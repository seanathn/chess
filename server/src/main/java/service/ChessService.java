package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataacess.DataAccessAuths;
import dataacess.DataAccessException;
import dataacess.DataAccessGames;
import dataacess.DataAccessUsers;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ChessService {

    private final DataAccessAuths authAccess = new DataAccessAuths();
    private final DataAccessGames gameAccess = new DataAccessGames();
    private final DataAccessUsers userAccess = new DataAccessUsers();

    public String registerRequest(UserData input) throws DataAccessException {
        UserData user = userAccess.findUserByUsername(input.username());

        if (user == null) {
            userAccess.addUser(input);
//             create authdata record and place in list
            return new Gson().toJson(createAuthData(input.username()));
        } else {
            throw new DataAccessException("Error: username already taken");
        }
    }

    public void clearRequest() {
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }

    public String loginRequest(UserData input) throws DataAccessException {
        UserData user = userAccess.findUserByUsername(input.username());
        if (user != null && Objects.equals(input.password(), user.password())) {
            return new Gson().toJson(createAuthData(input.username()));
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public void logoutRequest(String authToken) throws DataAccessException {
        AuthData auth = authAccess.findAuthByAuthToken(authToken);
        if (auth != null) {
            authAccess.removeAuth(auth);
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public ArrayList<GameData> listGamesRequest(String authToken) throws DataAccessException {
        if (authorized(authToken)) {
            return gameAccess.getListGames();
        }
        return null;
    }

    public int createGameRequest(String authToken, GameData game) throws DataAccessException {
        if (authorized(authToken)) {
            GameData newGame = new GameData(1234, "", "", game.gameName(), new ChessGame());
            gameAccess.addGame(newGame);
            return newGame.gameID();
        }
        return 0;
    }

    private AuthData createAuthData(String username) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authAccess.addAuth(auth);
        return auth;
    }

    private boolean authorized(String authToken) throws DataAccessException {
        if (authAccess.findAuthByAuthToken(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return true;
    }
}
