package dataacess;

import model.UserData;

import java.util.ArrayList;

public class DataAccessUsers {

    private final ArrayList<UserData> users = new ArrayList<>();

    public void addUser(UserData user) {
        users.add(user);
    }

    public ArrayList<UserData> getListUsers() {
        return users;
    }

    public void removeUser(UserData user) {
        users.remove(user);
    }
}
