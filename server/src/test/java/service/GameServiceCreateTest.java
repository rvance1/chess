package service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dto.CreateGameRequest;
import dto.CreateGameResult;
import exception.ServiceException;
import model.GameData;
import model.UserData;

public class GameServiceCreateTest extends GameServiceTestBase {

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