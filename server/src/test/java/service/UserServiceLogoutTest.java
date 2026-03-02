package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import exception.DataAccessException;
import exception.ServiceException;
import model.AuthData;

public class UserServiceLogoutTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        
        // auth in db
        authDAO.createAuth(new AuthData("t123", "kevin"));
    }

    @Test
    void logout_success_deletesToken() throws DataAccessException {
        userService.logout("t123");
        assertNull(authDAO.getAuth("t123"));
    }

    @Test
    void logout_missingToken_throws401() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.logout("   "));
        assertEquals(401, ex.getStatus());
    }

    @Test
    void logout_badToken_throws401() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.logout("nope"));
        assertEquals(401, ex.getStatus());
    }
}