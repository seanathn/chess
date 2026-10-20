package server;

import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;


public class Server {
    private ArrayList<UserData> users;
    private ArrayList<AuthData> auths;
    private ArrayList<GameData> games;

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context cxt) {
        // create userdata record and place in list
        UserData input = new UserData(cxt.pathParam("username"), cxt.pathParam("password"),cxt.pathParam("email"));
        if (!users.contains(input)) {
            users.add(input);
        } else {
            return;
        }
        // create authdata record and place in list
        AuthData auth = new AuthData(UUID.randomUUID().toString(), cxt.pathParam("username"));
        auths.add(auth);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
