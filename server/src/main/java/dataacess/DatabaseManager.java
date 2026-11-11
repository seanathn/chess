package dataacess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);

            var createUserData = """
                   CREATE TABLE IF NOT EXISTS user (username TEXT NOT NULL,
                   password TEXT NOT NULL,
                   email TEXT)
                   """;

            try (var createTableStatement = conn.prepareStatement(createUserData)) {
                createTableStatement.executeUpdate();
            }

            var createAuthTable = """
                    CREATE TABLE IF NOT EXISTS auth (authToken TEXT NOT NULL, username TEXT NOT NULL)
                    """;

            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
            }

            var createGameTable = """
                    CREATE TABLE IF NOT EXISTS game (gameID INT, gameName TEXT NOT NULL, whiteUsername TEXT,
                    blackUsername TEXT, chessGame TEXT)
                    """;

            try (var createTableStatement = conn.prepareStatement(createGameTable)) {
                createTableStatement.executeUpdate();
            }


            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to get connection", ex);
        }
    }

    public static void insertUser(Connection conn, String username, String password, String email) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) " +
                        "VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);

            preparedStatement.executeUpdate();
        }
    }

    public static UserData findByUsername(Connection conn, String username) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user " +
                "WHERE username = ?")) {
            preparedStatement.setString(1, username);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserData(resultSet.getString("username"),
                            resultSet.getString("password"), resultSet.getString("email"));
                } else {
                return null;
                }
            }
        }
    }

    public static void clearUserData(Connection conn) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
            preparedStatement.executeUpdate();
        }
    }

    public static void insertAuth(Connection conn, String username, String authToken) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (authToken, username) " +
                "VALUES(?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,authToken);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        }
    }

    public static AuthData findByAuthToken(Connection conn, String authToken) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("SELECT username, authToken FROM auth " +
                "WHERE authToken = ?")) {
            preparedStatement.setString(1, authToken);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new AuthData(resultSet.getString("authToken"),
                            resultSet.getString("username"));
                }
                return null;
            }
        }
    }

    public static void clearAuthData(Connection conn) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth")) {
            preparedStatement.executeUpdate();
        }
    }

    public static void deleteAuthData(Connection conn, AuthData auth) throws SQLException {
        // only looking at authToken right now, but may need to look at username too
        try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE authToken = ?")) {
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.executeUpdate();
        }
    }

    public static void insertGame(Connection conn, GameData game) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameID, " +
                "gameName, whiteUsername, blackUsername, chessGame) " +
                "VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1,game.gameID());
            preparedStatement.setString(2, game.gameName());
            preparedStatement.setString(3, game.whiteUsername());
            preparedStatement.setString(4, game.blackUsername());
            preparedStatement.setString(5, new Gson().toJson(game.game()));

            preparedStatement.executeUpdate();
        }
    }

    public static void clearGameData(Connection conn) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE game")) {
            preparedStatement.executeUpdate();
        }
    }

    public static void updateTeamUsername(Connection conn, String teamColor, String username,
                                          GameData game) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("UPDATE game SET " + teamColor + "=? WHERE gameID=?")) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, game.gameID());

            preparedStatement.executeUpdate();
        }
    }

    public static GameData findByGameID(Connection conn, int gameID) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("SELECT gameID, gameName, whiteUsername, " +
                "blackUsername, chessGame FROM game WHERE gameID = ?")) {
            preparedStatement.setInt(1, gameID);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new GameData(resultSet.getInt("gameID"),
                            resultSet.getString("gameName"), resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            new Gson().fromJson(resultSet.getString("chessGame"), ChessGame.class));
                }
                return null;
            }
        }
    }

    public static Collection<GameData> getListGames(Connection conn) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("SELECT * FROM game")) {
            try (var resultSet = preparedStatement.executeQuery()) {
                Collection<GameData> games = new ArrayList<>();
                while (resultSet.next()) {
                    games.add(new GameData(resultSet.getInt("gameID"),
                            resultSet.getString("gameName"), resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"), new Gson().fromJson(
                                    resultSet.getString("chessGame"), ChessGame.class)));
                }
                return games;
            }
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().
                getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
