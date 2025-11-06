package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearService;
import service.GameService;
import service.UserService;

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
    }

    private void registerUser(Context ctx) {
//        userService.register();
    }

    private void loginUser(Context ctx) {
//        userService.login();
    }

    private void logoutUser(Context ctx) {
//        userService.logout();
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
//        ctx.status(ex.toHttpStatusCode());
//        ctx.result(ex.toJson());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void stop() {
        javalin.stop();
    }
}
