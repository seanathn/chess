package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public class Server {
    private final ArrayList<UserData> users = new ArrayList<>();
    private final ArrayList<AuthData> auths = new ArrayList<>();
    private final ArrayList<GameData> games = new ArrayList<>();

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context cxt) {
        // create userdata record and place in list
        UserData input = getBodyObject(cxt, UserData.class);
        if (!users.contains(input)) {
            users.add(input);
//             create authdata record and place in list
            AuthData auth = new AuthData(UUID.randomUUID().toString(), input.username());
            auths.add(auth);
            cxt.result(new Gson().toJson(auth));
        } else {
            throw new ForbiddenResponse("Already Taken");
        }
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }
        return bodyObject;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
