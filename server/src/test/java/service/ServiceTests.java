package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

// Used ChatGPT to write the unit test

class ServiceTests {
    private static final Gson GSON = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private Server server;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        server = new Server();
        baseUrl = "http://localhost:" + server.run(0);
        request("DELETE", "/db", null, null);
    }

    @AfterEach
    void tearDown() {
        if (server != null) server.stop();
    }

    @Test
    void registerReturnsAuthData() {
        HttpResponse<String> response = request("POST", "/user", userJson("alice", "secret"), null);
        assertEquals(200, response.statusCode());
        JsonObject auth = responseObject(response.body());
        assertEquals("alice", auth.get("username").getAsString());
        assertFalse(auth.get("authToken").getAsString().isBlank());
    }

    @Test
    void duplicateRegistrationReturns403() {
        register("alice");
        HttpResponse<String> response = request("POST", "/user", userJson("alice", "other"), null);
        assertEquals(403, response.statusCode());
        assertTrue(response.body().contains("message"));
    }

    @Test
    void registrationWithMissingPasswordReturns400() {
        HttpResponse<String> response = request("POST", "/user", "{\"username\":\"alice\"}", null);
        assertEquals(400, response.statusCode());
    }

    @Test
    void loginWithValidCredentialsReturnsAuthData() {
        register("alice");
        HttpResponse<String> response = request("POST", "/session", userJson("alice", "secret"), null);
        assertEquals(200, response.statusCode());
        assertEquals("alice", responseObject(response.body()).get("username").getAsString());
    }

    @Test
    void loginWithInvalidCredentialsReturns401() {
        register("alice");
        HttpResponse<String> response = request("POST", "/session", userJson("alice", "wrong"), null);
        assertEquals(401, response.statusCode());
    }

    @Test
    void logoutInvalidatesToken() {
        String token = register("alice");
        assertEquals(200, request("DELETE", "/session", null, token).statusCode());
        assertEquals(401, request("GET", "/game", null, token).statusCode());
    }

    @Test
    void listGamesReturnsEmptyArrayForAuthorizedUser() {
        String token = register("alice");
        HttpResponse<String> response = request("GET", "/game", null, token);
        assertEquals(200, response.statusCode());
        assertTrue(responseObject(response.body()).getAsJsonArray("games").isEmpty());
    }

    @Test
    void listGamesWithoutValidTokenReturns401() {
        assertEquals(401, request("GET", "/game", null, "invalid").statusCode());
    }

    @Test
    void createGameReturnsGameId() {
        String token = register("alice");
        HttpResponse<String> response = request("POST", "/game", "{\"gameName\":\"Friendly match\"}", token);
        assertEquals(200, response.statusCode());
        assertEquals(1, responseObject(response.body()).get("gameID").getAsInt());
    }

    @Test
    void createGameWithoutNameReturns400() {
        String token = register("alice");
        assertEquals(400, request("POST", "/game", "{}", token).statusCode());
    }

    @Test
    void createGameWithoutValidTokenReturns401() {
        assertEquals(401, request("POST", "/game", "{\"gameName\":\"Match\"}", "invalid").statusCode());
    }

    @Test
    void joinGameAssignsPlayerToRequestedTeam() {
        String token = register("alice");
        HttpResponse<String> created = request("POST", "/game", "{\"gameName\":\"Match\"}", token);
        int id = responseObject(created.body()).get("gameID").getAsInt();

        HttpResponse<String> joined = request("PUT", "/game",
                "{\"playerColor\":\"WHITE\",\"gameID\":" + id + "}", token);
        assertEquals(200, joined.statusCode());

        JsonObject game = responseObject(request("GET", "/game", null, token).body())
                .getAsJsonArray("games").get(0).getAsJsonObject();
        assertEquals("alice", game.get("whiteUsername").getAsString());
    }

    @Test
    void joiningAnOccupiedTeamReturns403() {
        String alice = register("alice");
        String bob = register("bob");
        int id = responseObject(request("POST", "/game", "{\"gameName\":\"Match\"}", alice).body())
                .get("gameID").getAsInt();

        request("PUT", "/game", "{\"playerColor\":\"WHITE\",\"gameID\":" + id + "}", alice);
        HttpResponse<String> response = request("PUT", "/game",
                "{\"playerColor\":\"WHITE\",\"gameID\":" + id + "}", bob);
        assertEquals(403, response.statusCode());
    }

    @Test
    void joiningUnknownGameReturns400() {
        String token = register("alice");
        HttpResponse<String> response = request("PUT", "/game",
                "{\"playerColor\":\"WHITE\",\"gameID\":999}", token);
        assertEquals(400, response.statusCode());
    }

    private String register(String username) {
        HttpResponse<String> response = request("POST", "/user", userJson(username, "secret"), null);
        assertEquals(200, response.statusCode());
        return responseObject(response.body()).get("authToken").getAsString();
    }

    private static String userJson(String username, String password) {
        return GSON.toJson(new UserPayload(username, password, username + "@example.com"));
    }

    private HttpResponse<String> request(String method, String path, String body, String token) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json");
            if (token != null) builder.header("authorization", token);
            builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(body));
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new AssertionError("Request failed: " + method + " " + path, e);
        }
    }

    private static JsonObject responseObject(String body) {
        var parsed = JsonParser.parseString(body);
        // Handle Javalin versions that JSON-encode the String returned by the service.
        if (parsed.isJsonPrimitive() && parsed.getAsJsonPrimitive().isString()) {
            parsed = JsonParser.parseString(parsed.getAsString());
        }
        return parsed.getAsJsonObject();
    }

    private record UserPayload(String username, String password, String email) { }
    }
