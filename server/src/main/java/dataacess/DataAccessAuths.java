package dataacess;

import model.AuthData;

import java.util.ArrayList;

public class DataAccessAuths {

    private final ArrayList<AuthData> auths = new ArrayList<>();

    public void addAuth(AuthData auth) {
        auths.add(auth);
    }

    public ArrayList<AuthData> getListAuths() {
        return auths;
    }

    public void clear() {
        auths.clear();
    }

    public AuthData findAuthByUsername(String username) {
        for (AuthData auth : auths) {
            if (auth.username().equals(username)) {
                return auth;
            }
        }
        return null;
    }

    public void removeAuth(AuthData auth) {
        auths.remove(auth);
    }
}
