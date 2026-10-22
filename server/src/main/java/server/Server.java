package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.ChessService;

import java.util.Map;


public class Server {

    private final Javalin javalin;
    private final ChessService chessService = new ChessService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clearDB)
                .post("/session", this::login)
                .delete("/session", this::logout);
        // Register your endpoints and exception handlers here.
    }

    private void register(Context cxt) {
        try {
            UserData user = getBodyObject(cxt, UserData.class);
            String result = chessService.registerRequest(user);

            cxt.status(200);
            cxt.json(result);
        } catch (Exception e) {
            cxt.status(403);
            cxt.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }

    }

    private void clearDB(Context cxt) {
        chessService.clearRequest();
        cxt.status(200);
    }

    private void login(Context cxt) {
        try {
            UserData user = getBodyObject(cxt, UserData.class);
            String result = chessService.loginRequest(user);

            cxt.status(200);
            cxt.json(result);
        } catch (Exception e) {
            cxt.status(401);
            cxt.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void logout(Context cxt) {
        try {
            String authToken = cxt.header("authorization");
            chessService.logoutRequest(authToken);
        } catch (Exception e) {
            cxt.status(401);
            cxt.json(new Gson().toJson(Map.of("message", e.getMessage())));
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
