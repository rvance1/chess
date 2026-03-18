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

}
