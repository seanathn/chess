package datacess.sql;

import chess.ChessGame;
import dataacess.DataAccessException;
import dataacess.sql.DatabaseManager;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {

    @Test
    public void testAddUser() throws SQLException, DataAccessException {

        String username = "testUser123";
        String password = "password123";
        String email = "test@example.com";

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertUser(conn, username, password, email);

        conn.close();

        conn = DatabaseManager.getConnection();

        UserData user = DatabaseManager.findByUsername(conn, username);

        DatabaseManager.clearUserData(conn);

        conn.close();

        assertNotNull(user);
        assertEquals(username, user.username());
        assertEquals(password, user.password());
        assertEquals(email, user.email());
    }

    @Test
    public void testEncryptingPassword() throws SQLException, DataAccessException {

        String username = "testUser123";
        String password = "password123";
        String email = "test@example.com";

        String encyPass = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertUser(conn, username, encyPass, email);

        conn.close();

        conn = DatabaseManager.getConnection();

        UserData user = DatabaseManager.findByUsername(conn, username);

        DatabaseManager.clearUserData(conn);

        conn.close();

        assertNotNull(user);
        var match = BCrypt.checkpw(password, user.password()) ? "==" : "!=";

        assertNotNull(user);
        assertEquals(username, user.username());
        assertEquals("==", match);
        assertEquals(email, user.email());
    }


    @Test
    public void testClearUserData() throws DataAccessException, SQLException {
        String username = "testUser123";
        String password = "password123";
        String email = "test@example.com";

        String encyPass = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertUser(conn, username, encyPass, email);

        conn.close();

        conn = DatabaseManager.getConnection();

        UserData user = DatabaseManager.findByUsername(conn, username);

        conn.close();

        assertNotNull(user);

        conn = DatabaseManager.getConnection();

        DatabaseManager.clearUserData(conn);

        user = DatabaseManager.findByUsername(conn, username);

        assertNull(user);
    }

    @Test
    public void testAddAuth() throws SQLException, DataAccessException {

        String username = "testUser123";
        String authToken = "password123";

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertAuth(conn, username, authToken);

        conn.close();

        conn = DatabaseManager.getConnection();

        AuthData auth = DatabaseManager.findByAuthToken(conn, authToken);

        conn.close();

        conn = DatabaseManager.getConnection();

        DatabaseManager.clearAuthData(conn);

        assertNotNull(auth);
        assertEquals(username, auth.username());
        assertEquals(authToken, auth.authToken());
    }

    @Test
    public void testDeleteAuth() throws SQLException, DataAccessException {
        String username = "testUser123";
        String authToken = "password123";

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertAuth(conn, username, authToken);

        AuthData auth = DatabaseManager.findByAuthToken(conn, authToken);

        assertNotNull(auth);

        DatabaseManager.deleteAuthData(conn, auth);

        auth = DatabaseManager.findByAuthToken(conn, authToken);

        conn.close();

        assertNull(auth);
    }

    @Test
    public void testGameAdd() throws SQLException, DataAccessException {
        GameData game = new GameData(47, "dude", "where", "is", new ChessGame());

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertGame(conn, game);

        DatabaseManager.clearGameData(conn);

        conn.close();
    }

    @Test
    public void testSetUser() throws SQLException, DataAccessException {
        GameData game = new GameData(47, "dude", "where", "is", new ChessGame());

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertGame(conn, game);

        DatabaseManager.updateTeamUsername(conn, "whiteUsername", "car", game);

        DatabaseManager.clearGameData(conn);

        conn.close();
    }

    @Test
    public void testFindGameByGameID() throws SQLException, DataAccessException {
        GameData game = new GameData(47, "dude", "where", "is", new ChessGame());

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertGame(conn, game);

        GameData databaseGame = DatabaseManager.findByGameID(conn, game.gameID());

        conn.close();

        assertNotNull(databaseGame);

        assertEquals(databaseGame, game);
    }

    @Test
    public void testEmptyGameList() throws SQLException, DataAccessException {
        Connection conn = DatabaseManager.getConnection();
        DatabaseManager.clearGameData(conn);
        Collection<GameData> empty = DatabaseManager.getListGames(conn);

        Collection<GameData> test = new ArrayList<>();

        assertEquals(empty, test);
    }

    @Test
    public void testGameList() throws SQLException, DataAccessException {
        GameData game = new GameData(47, "dude", "where", "is", new ChessGame());
        GameData game2 = new GameData(66, "a", "where", "d", new ChessGame());
        GameData game4 = new GameData(55, "ad", "d", "aw", new ChessGame());

        Collection<GameData> games = List.of(new GameData[]{game, game2, game4});

        Connection conn = DatabaseManager.getConnection();

        DatabaseManager.insertGame(conn, game);

        DatabaseManager.insertGame(conn, game2);

        DatabaseManager.insertGame(conn, game4);

        Collection<GameData> databaseGames = DatabaseManager.getListGames(conn);

        conn.close();

        assertEquals(databaseGames, games);
    }
}
