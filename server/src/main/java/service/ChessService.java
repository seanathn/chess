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
        ArrayList<UserData> users = userAccess.getListUsers();

        if (!users.contains(input)) {
            users.add(input);
//             create authdata record and place in list
            AuthData auth = new AuthData(UUID.randomUUID().toString(), input.username());
            authAccess.addAuth(auth);
            return new Gson().toJson(auth);
        } else {
            throw new ForbiddenResponse("Error: username already taken");
        }
    }
}
