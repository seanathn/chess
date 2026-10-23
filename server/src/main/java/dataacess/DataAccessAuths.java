package dataacess;

import model.AuthData;

import java.util.ArrayList;

public class DataAccessAuths {

    private final ArrayList<AuthData> auths = new ArrayList<>();

    public void addAuth(AuthData auth) {
        auths.add(auth);
    }

    public void clear() {
        auths.clear();
    }

    public AuthData findAuthByAuthToken(String authToken) {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    public void removeAuth(AuthData auth) {
        auths.remove(auth);
    }
}
