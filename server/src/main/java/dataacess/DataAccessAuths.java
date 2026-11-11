package dataacess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class DataAccessAuths {

//    private final ArrayList<AuthData> auths = new ArrayList<>();
    private Connection conn;

    public void addAuth(AuthData auth) throws DataAccessException, SQLException {
//        auths.add(auth);
        conn = DatabaseManager.getConnection();
        DatabaseManager.insertAuth(conn, auth.username(), auth.authToken());
        conn.close();
    }

    public void clear() throws DataAccessException, SQLException {
//        auths.clear();
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearAuthData(conn);
        conn.close();
    }

    public AuthData findAuthByAuthToken(String authToken) throws DataAccessException, SQLException {
//        for (AuthData auth : auths) {
//            if (auth.authToken().equals(authToken)) {
//                return auth;
//            }
//        }
        conn = DatabaseManager.getConnection();
        AuthData data = DatabaseManager.findByAuthToken(conn, authToken);
        conn.close();
        return data;
    }

    public void removeAuth(AuthData auth) throws SQLException, DataAccessException {
//        auths.remove(auth);
        conn = DatabaseManager.getConnection();
        DatabaseManager.deleteAuthData(conn, auth);
        conn.close();
    }
}
