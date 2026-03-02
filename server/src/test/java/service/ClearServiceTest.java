package service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

class ClearServiceTest {
    @Test
    void clearClearsAllDAOs() throws Exception {
        // arrange
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        userDAO.insertUser(new UserData("u", "p", "e"));
        int gameId = gameDAO.createGame(new GameData(0, null, null, "game", null));
        authDAO.createAuth(new AuthData("token", "u"));

        // Sanity check
        assertNotNull(userDAO.getUser("u"));
        assertFalse(gameDAO.listGames().isEmpty());
        assertNotNull(authDAO.getAuth("token"));

        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        clearService.clear();

        assertNull(userDAO.getUser("u"));
        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(authDAO.getAuth("token"));
        assertNull(gameDAO.getGame(gameId));
    }
}
