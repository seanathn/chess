package service;

import com.google.gson.Gson;
import dataacess.DataAccessAuths;
import dataacess.DataAccessGames;
import dataacess.DataAccessUsers;
import io.javalin.http.ForbiddenResponse;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class ChessService {

    private final DataAccessAuths authAccess = new DataAccessAuths();
    private final DataAccessGames gameAccess = new DataAccessGames();
    private final DataAccessUsers userAccess = new DataAccessUsers();

    public String registerRequest(UserData input) {
        UserData user = userAccess.findUserByUsername(input.username());

        if (user == null) {
            userAccess.addUser(input);
//             create authdata record and place in list
            AuthData auth = createAuthData(input.username());
            authAccess.addAuth(auth);
            return new Gson().toJson(auth);
        } else {
            throw new ForbiddenResponse("Error: username already taken");
        }
    }

    public void clearRequest() {
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }


    private AuthData createAuthData(String username) {
        return new AuthData(UUID.randomUUID().toString(), username);
    }
}
