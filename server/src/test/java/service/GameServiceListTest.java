package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.CreateGameRequest;
import dto.ListGamesResult;
import exception.ServiceException;
import model.UserData;

public class GameServiceListTest extends GameServiceTestBase {

    @BeforeEach
    void setUpListTest() throws Exception {
        super.setUp();
    }

    @Test
    void listGamesSuccessReturnsCreatedGames() throws Exception {
        var reg = userService.register(new UserData("u", "p", "e"));
        String token = reg.authToken();

        var g1 = gameService.createGame(token, new CreateGameRequest("g1"));
        var g2 = gameService.createGame(token, new CreateGameRequest("g2"));

        ListGamesResult res = gameService.listGames(token);

        assertNotNull(res);
        assertNotNull(res.games());
        assertEquals(2, res.games().size());
        assertTrue(res.games().stream().anyMatch(g -> g.gameID() == g1.gameID() && g.gameName().equals("g1")));
        assertTrue(res.games().stream().anyMatch(g -> g.gameID() == g2.gameID() && g.gameName().equals("g2")));
    }

    @Test
    void listGamesUnauthorizedMissingToken() {
        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.listGames(null));
        assertEquals("Error: unauthorized", ex.getMessage());
    }
}