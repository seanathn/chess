package service;

import com.google.gson.Gson;
import dataacess.DataAccessAuths;
import dataacess.DataAccessGames;
import dataacess.DataAccessUsers;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;
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
            return new Gson().toJson(createAuthData(input.username()));
        } else {
            throw new ForbiddenResponse("Error: username already taken");
        }
    }

    public void clearRequest() {
        authAccess.clear();
        gameAccess.clear();
        userAccess.clear();
    }

    public String loginRequest(UserData input) {
        UserData user = userAccess.findUserByUsername(input.username());
        if (authAccess.findAuthByUsername(input.username()) != null) {
            throw new UnauthorizedResponse("Error: unauthorized");
        }
        if (user != null && Objects.equals(input.password(), user.password())) {
            return new Gson().toJson(createAuthData(input.username()));
        } else {
            throw new UnauthorizedResponse("Error: unauthorized");
        }
    }


    private AuthData createAuthData(String username) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authAccess.addAuth(auth);
        return auth;
    }
}
