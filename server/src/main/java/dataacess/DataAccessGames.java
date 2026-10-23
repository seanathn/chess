package dataacess;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class DataAccessGames {

    private final ArrayList<GameData> games = new ArrayList<>();

    public void addGame(GameData game) {
        games.add(game);
    }

    public ArrayList<GameData> getListGames() {
        return games;
    }

    public GameData findGameByID(int gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public void setTeamUser(String teamColor, String username, GameData game) {
        games.remove(game);
        if (Objects.equals(teamColor, "WHITE")) {
            games.add(game.setWhiteTeam(username));
        } else {
            games.add(game.setBlackTeam(username));
        }
    }

    public void clear() {
        games.clear();
    }

}
