package service;

import org.junit.jupiter.api.BeforeEach;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import exception.DataAccessException;

public class GameServiceTestBase {

    protected UserDAO userDAO;
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;

    protected UserService userService;
    protected GameService gameService;

    @BeforeEach
    protected void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
    }
}
