package dataacess;

import dataacess.sql.DatabaseManager;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class DataAccessUsers {

    private Connection conn;

    public void addUser(UserData user) throws SQLException, DataAccessException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.insertUser(conn, user.username(), user.password(), user.email());
        conn.close();
    }

    public UserData findUserByUsername(String username) throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        UserData result = DatabaseManager.findByUsername(conn, username);
        conn.close();
        return result;
    }

    public void clear() throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearUserData(conn);
        conn.close();
    }
}
