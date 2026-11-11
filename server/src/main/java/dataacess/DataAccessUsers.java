package dataacess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class DataAccessUsers {

//    private final ArrayList<UserData> users = new ArrayList<>();
    private Connection conn;

    public void addUser(UserData user) throws SQLException, DataAccessException {
//        users.add(user);
        conn = DatabaseManager.getConnection();
        DatabaseManager.insertUser(conn, user.username(), user.password(), user.email());
        conn.close();
    }

    public UserData findUserByUsername(String username) throws DataAccessException, SQLException {
//        for (UserData user : users) {
//            if (Objects.equals(user.username(), username)) {
//                return user;
//            }
//        }
        conn = DatabaseManager.getConnection();
        UserData result = DatabaseManager.findByUsername(conn, username);
        conn.close();
        return result;
//        return null;
    }

    public void clear() throws DataAccessException, SQLException {
//        users.clear();
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearUserData(conn);
        conn.close();
    }
}
