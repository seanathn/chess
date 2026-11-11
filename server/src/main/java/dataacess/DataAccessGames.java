package dataacess;

import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class DataAccessGames {

//    private final ArrayList<GameData> games = new ArrayList<>();
    private Connection conn;

    public void addGame(GameData game) throws DataAccessException, SQLException {
//        games.add(game);
        conn = DatabaseManager.getConnection();
        DatabaseManager.insertGame(conn, game);
        conn.close();
    }

    public ArrayList<GameData> getListGames() throws DataAccessException, SQLException {
//        return games;
        conn = DatabaseManager.getConnection();
        ArrayList<GameData> games = (ArrayList<GameData>) DatabaseManager.getListGames(conn);
        conn.close();
        return games;
    }

    public GameData findGameByID(int gameID) throws DataAccessException, SQLException {
//        for (GameData game : games) {
//            if (game.gameID() == gameID) {
//                return game;
//            }
//        }
//        return null;
        conn = DatabaseManager.getConnection();
        GameData game = DatabaseManager.findByGameID(conn, gameID);
        conn.close();
        return game;
    }

    public void setTeamUser(String teamColor, String username, GameData game) throws DataAccessException, SQLException {
//        games.remove(game);
        conn = DatabaseManager.getConnection();
        if (Objects.equals(teamColor, "WHITE")) {
//            games.add(game.setWhiteTeam(username));
            DatabaseManager.updateTeamUsername(conn, "whiteUsername", username, game);
        } else {
//            games.add(game.setBlackTeam(username));
            DatabaseManager.updateTeamUsername(conn, "blackUsername", username, game);
        }
        conn.close();
    }

    public void clear() throws DataAccessException, SQLException {
//        games.clear();
        conn = DatabaseManager.getConnection();
        DatabaseManager.clearGameData(conn);
        conn.close();
    }

}
