package dataacess;

import model.UserData;
import java.sql.*;
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
            throw new DataAccessException("failed to create database", ex);
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
                    blackUsername TEXT, turnColor TEXT)
                    """;

            try (var createTableStatement = conn.prepareStatement(createGameTable)) {
                createTableStatement.executeUpdate();
            }

            var createBoardTable = """
                    CREATE TABLE IF NOT EXISTS board (gameID INT, rowNum INT NOT NULL, colNum INT NOT NULL,
                    teamColor CHAR, pieceType VARCHAR(2))
                    """;

            try (var createTableStatement = conn.prepareStatement(createBoardTable)) {
                createTableStatement.executeUpdate();
            } catch (Exception e) {
                throw new SQLException("Issue Here");
            }


            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
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
                while (resultSet.next()) {
                    return new UserData(resultSet.getString("username"),
                            resultSet.getString("password"), resultSet.getString("email"));
                }
                return null;
            }
        }
    }

    public static void clearUserData(Connection conn) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE user")) {
            preparedStatement.executeUpdate();
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
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
