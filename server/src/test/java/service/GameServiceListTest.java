package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.CreateGameRequest;
import dto.ListGamesResult;
import exception.ServiceException;
import model.UserData;

public class GameServiceListTest {

    private UserService userService;
    private GameService gameService;

    private UserDAO userDAO; 
    private AuthDAO authDAO; 
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() throws Exception {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
    }

    @Test
    void listGames_success_returnsCreatedGames() throws Exception {
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
    void listGames_unauthorized_missingToken() {
        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.listGames(null));
        assertEquals("Error: unauthorized", ex.getMessage());
    }
}