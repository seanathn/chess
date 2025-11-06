package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.response.RegisterResult;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;
import java.util.UUID;

public class Server {

    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;
//    private final WebSocketHandler webSocketHandler;
    private final Javalin javalin;

    public Server() {
        // this may be incorrect because I am making new Memory Data points, and they may not be connected
        this(new UserService(new MemoryDataAccess()), new ClearService(new MemoryDataAccess()),
                new GameService(new MemoryDataAccess()));
    }

    public Server(UserService userService, ClearService clearService, GameService gameService) {
        this.userService = userService;
        this.clearService = clearService;
        this.gameService = gameService;


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::deleteAllData)
                .post("/user", this::registerUser)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .get("/game", this::getGamesList)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(DataAccessException.class, this::exceptionHandler);

        // Register your endpoints and exception handlers here.

//                .ws("/ws", ws -> {
//                    ws.onConnect(webSocketHandler);
//                    ws.onMessage(webSocketHandler);
//                    ws.onClose(webSocketHandler);
//                });

    }

    private void deleteAllData(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.result();
    }

    private void registerUser(Context ctx) {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        try {
            ctx.status(200);
            ctx.result(new Gson().toJson(userService.register(registerRequest)));
        } catch (DataAccessException e) {
            exceptionHandler(e, ctx);
        }
    }

    private void loginUser(Context ctx) {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        try {
            ctx.result(new Gson().toJson(userService.login(loginRequest)));

        } catch (DataAccessException e) {
            exceptionHandler(e, ctx);
        }

    }

    private void logoutUser(Context ctx) {
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
        try {
            userService.logout(logoutRequest);
            ctx.result();
        } catch (DataAccessException e) {
            exceptionHandler(e, ctx);
        }
    }

    private void getGamesList(Context ctx) {
//        gameService.getGames();
    }

    private void createGame(Context ctx) {
//        gameService.createGame();
    }

    private void joinGame(Context ctx) {
//        gameService.joinGame();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", ex.getMessage()), "success", false));
        if (ex.getMessage().equals("bad request")) {
            ctx.status(400);
        } else if (ex.getMessage().equals("unauthorized")) {
            ctx.status(401);
        } else if (ex.getMessage().equals("already taken")) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }
        ctx.json(body);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
