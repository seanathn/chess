package dataacess;

import dataacess.sql.DatabaseManager;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class DataAccessGames {

    private Connection conn;

    public void addGame(GameData game) throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.insertGame(conn, game);
        conn.close();
    }

    public ArrayList<GameData> getListGames() throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        ArrayList<GameData> games = (ArrayList<GameData>) DatabaseManager.getListGames(conn);
        conn.close();
        return games;
    }

    public GameData findGameByID(int gameID) throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        GameData game = DatabaseManager.findByGameID(conn, gameID);
        conn.close();
        return game;
    }

    public void setTeamUser(String teamColor, String username, GameData game) throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        if (Objects.equals(teamColor, "WHITE")) {
            DatabaseManager.updateTeamUsername(conn, "whiteUsername", username, game);
        } else {
            DatabaseManager.updateTeamUsername(conn, "blackUsername", username, game);
        }
        conn.close();
    }

    public void clear() throws DataAccessException, SQLException {
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearGameData(conn);
        conn.close();
    }

}
