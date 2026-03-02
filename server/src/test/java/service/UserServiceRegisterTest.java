package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import handler.results.RegisterResult;
import model.UserData;

public class UserServiceRegisterTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        // daos
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        // clear
        userDAO.clear();
        authDAO.clear();

        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() throws Exception {
        UserData req = new UserData("abc", "password", "bob@email.com");

        RegisterResult res = userService.register(req);

        assertEquals("abc", res.username());
        assertNotNull(res.authToken());
        assertFalse(res.authToken().isBlank());

        assertNotNull(userDAO.getUser("abc"));
    }

    @Test
    void registerAlreadyTaken() throws Exception {
        UserData req = new UserData("bob", "password", "bob@email.com");

        userService.register(req);

        assertThrows(AlreadyTakenException.class, () -> userService.register(req));
    }

    // Optional but helpful
    @Test
    void registerBadRequestMissingField() {
        UserData req = new UserData("", "password", "bob@email.com");
        assertThrows(BadRequestException.class, () -> userService.register(req));
    }
}