package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the DataAccess interface.
 * Stores users, games, and auth tokens in maps.
 * Used for Phase 3 before the SQL database implementation.
 */
public class MemoryDataAccess implements DataAccess {

    private final Map<String, UserData> users = new ConcurrentHashMap<>();
    private final Map<Integer, GameData> games = new ConcurrentHashMap<>();
    private final Map<String, AuthData> authTokens = new ConcurrentHashMap<>();


//    public MemoryDataAccess() {}

    @Override
    public void clear() {
        users.clear();
        games.clear();
        authTokens.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        throw new DataAccessException("User not found");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        else {
            throw new DataAccessException("No game found");
        }
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authTokens.containsKey(auth.authToken())) {
            throw new DataAccessException("Auth token already exists");
        }
        authTokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            return authTokens.get(authToken);
        }
        throw new DataAccessException("Invalid auth");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            authTokens.remove(authToken);
        }
        else {
            throw new DataAccessException("Invalid auth");
        }
    }
}
