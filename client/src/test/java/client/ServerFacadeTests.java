package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.ResponseException;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();

        var port = server.run(0);
        facade = new ServerFacade(port);

        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositive() throws Exception {
        var authData = facade.register("kevin", "password123", "kevin@email.com");

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("kevin", authData.username());
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register("kevin", "password123", "kevin@email.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("kevin", "differentPassword", "other@email.com");
        });
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("kevin", "password123", "kevin@email.com");

        var authData = facade.login("kevin", "password123");

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginNegative() throws Exception {
        facade.register("kevin", "password123", "kevin@email.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("kevin", "wrongPassword");
        });
    }

    @Test
    public void logoutPositive() throws Exception {
        var authData = facade.register("kevin", "password123", "kevin@email.com");

        Assertions.assertDoesNotThrow(() -> {
            facade.logout(authData.authToken());
        });
    }

    @Test
    public void logoutNegative() throws Exception {
        facade.register("kevin", "password123", "kevin@email.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout("badAuthToken");
        });
    }

    @Test
    public void createGamePositive() throws Exception {
        var auth = facade.register("kevin", "password123", "kevin@email.com");

        int gameID = facade.createGame(auth.authToken(), "test game");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameNegative() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.createGame("badAuthToken", "test game");
        });
    }

    @Test
    public void listGamesPositive() throws Exception {
        var auth = facade.register("kevin", "password123", "kevin@email.com");
        facade.createGame(auth.authToken(), "game1");
        facade.createGame(auth.authToken(), "game2");

        var games = facade.listGames(auth.authToken());

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.size() >= 2);
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGames("badAuthToken");
        });
    }

    @Test
    public void joinGamePositive() throws Exception {
        var auth = facade.register("kevin", "password123", "kevin@email.com");
        int gameID = facade.createGame(auth.authToken(), "test game");

        Assertions.assertDoesNotThrow(() -> {
            facade.joinGame(auth.authToken(), "WHITE", gameID);
        });
    }

    @Test
    public void joinGameNegative() throws Exception {
        var auth = facade.register("kevin", "password123", "kevin@email.com");
        int gameID = facade.createGame(auth.authToken(), "test game");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame("badAuthToken", "WHITE", gameID);
        });
    }
}
