package service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import dto.CreateGameResult;
import exception.DataAccessException;
import exception.ServiceException;
import model.GameData;
import model.UserData;

public class GameServiceCreateTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void createGameSuccessCreatesGameAndReturnsId() throws Exception {
        // register for token
        UserData newUser = new UserData("kevin", "pw", "email");
        var regRes = userService.register(newUser);
        String token = regRes.authToken();
        assertNotNull(token);
        assertFalse(token.isBlank());

        // Act
        CreateGameResult res = gameService.createGame(token, new CreateGameRequest("my game"));

        // returned gameID looks valid
        assertNotNull(res);
        assertTrue(res.gameID() > 0);

        // game exists in DAO with correct defaults
        GameData stored = gameDAO.getGame(res.gameID());
        assertNotNull(stored);
        assertEquals(res.gameID(), stored.gameID());
        assertEquals("my game", stored.gameName());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
        assertNotNull(stored.game());
    }

    @Test
    void createGameUnauthorizedWhenTokenMissing() {
        ServiceException ex = assertThrows(ServiceException.class, () ->
                gameService.createGame(null, new CreateGameRequest("my game"))
        );

        assertEquals(401, ex.getStatus());

        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void createGameUnauthorizedWhenTokenInvalid() {
        String fakeToken = UUID.randomUUID().toString();

        ServiceException ex = assertThrows(ServiceException.class, () ->
                gameService.createGame(fakeToken, new CreateGameRequest("my game"))
        );

        
        assertEquals(401, ex.getStatus());
        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void createGameBadRequestWhenGameNameBlank() throws Exception {
        // get token
        UserData newUser = new UserData("kevin", "pw", "email");
        var regRes = userService.register(newUser);
        String token = regRes.authToken();

        ServiceException ex = assertThrows(ServiceException.class, () ->
                gameService.createGame(token, new CreateGameRequest("   "))
        );

        assertEquals(400, ex.getStatus());
        assertTrue(ex.getMessage().contains("bad request"));
    }
}