package dataacess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class DataAccessUsers {

    private final ArrayList<UserData> users = new ArrayList<>();

    public void addUser(UserData user) {
        users.add(user);
    }

    public UserData findUserByUsername(String username) {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<UserData> getListUsers() {
        return users;
    }

    public void removeUser(UserData user) {
        users.remove(user);
    }

    public void clear() {
        users.clear();
    }
}
