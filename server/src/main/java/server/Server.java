package server;

import com.google.gson.Gson;
import dataacess.AlreadyTakenException;
import dataacess.BadRequestError;
import dataacess.UnauthorizedException;
import io.javalin.*;
import io.javalin.http.Context;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import service.ChessService;

import java.util.Map;

import static java.util.Collections.emptyMap;


public class Server {

    private final Javalin javalin;
    private final ChessService chessService = new ChessService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .delete("/db", this::clearDB)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("game", this::joinGame);
        // Register your endpoints and exception handlers here.
    }

    private void register(Context ctx) {
        try {
            UserData user = getBodyObject(ctx, UserData.class);
            String result = chessService.registerRequest(user);

            ctx.status(200);
            ctx.json(result);
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (BadRequestError e) {
            ctx.status(400);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }

    }

    private void clearDB(Context ctx) {
        chessService.clearRequest();
        ctx.status(200);
        ctx.json(new Gson().toJson(emptyMap()));
    }

    private void login(Context ctx) {
        try {
            UserData user = getBodyObject(ctx, UserData.class);
            String result = chessService.loginRequest(user);

            ctx.status(200);
            ctx.json(result);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (BadRequestError e) {
            ctx.status(400);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            chessService.logoutRequest(authToken);

            ctx.status(200);
            ctx.json(new Gson().toJson(emptyMap()));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            ctx.status(200);
            ctx.json(new Gson().toJson(Map.of("games", chessService.listGamesRequest(authToken))));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            GameData game = getBodyObject(ctx, GameData.class);

            int result = chessService.createGameRequest(authToken, game);
            ctx.status(200);
            ctx.json(new Gson().toJson(Map.of("gameID", result)));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (BadRequestError e) {
            ctx.status(400);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = getBodyObject(ctx, JoinGameRequest.class);
            chessService.joinGameRequest(authToken, request);

            ctx.status(200);
            ctx.json(new Gson().toJson(emptyMap()));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (BadRequestError e) {
            ctx.status(400);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new Gson().toJson(Map.of("message", e.getMessage())));
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
