package dataacess;

import dataacess.sql.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class DataAccessAuths {

    private Connection conn;

    public void addAuth(AuthData auth) throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.insertAuth(conn, auth.username(), auth.authToken());
        conn.close();
    }

    public void clear() throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearAuthData(conn);
        conn.close();
    }

    public AuthData findAuthByAuthToken(String authToken) throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        AuthData data = DatabaseManager.findByAuthToken(conn, authToken);
        conn.close();
        return data;
    }

    public void removeAuth(AuthData auth) throws SQLException, DataAccessException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.deleteAuthData(conn, auth);
        conn.close();
    }
}
