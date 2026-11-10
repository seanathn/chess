package datacess.sql;

import dataacess.DataAccessException;
import dataacess.DatabaseManager;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class sqlTests {

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
}
