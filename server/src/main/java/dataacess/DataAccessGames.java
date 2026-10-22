package dataacess;

import model.GameData;

import java.util.ArrayList;

public class DataAccessGames {

    private ArrayList<GameData> games = new ArrayList<>();

    public void addGame(GameData game) {
        games.add(game);
    }

    public ArrayList<GameData> getListGames() {
        return games;
    }


    public void clear() {
        games.clear();
    }

    public void removeGame(GameData game) {
        games.remove(game);
    }
}
