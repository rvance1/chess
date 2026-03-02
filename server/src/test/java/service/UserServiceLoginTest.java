package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.LoginRequest;
import dto.LoginResult;
import exception.DataAccessException;
import exception.ServiceException;
import model.AuthData;
import model.UserData;

public class UserServiceLoginTest {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        // user in db
        userDAO.insertUser(new UserData("kevin", "pw123", "k@x.com"));
    }

    @Test
    void login_success_returnsToken_andStoresAuth() throws DataAccessException {
        LoginResult res = userService.login(new LoginRequest("kevin", "pw123"));

        // returned data ok
        assertEquals("kevin", res.username());
        assertNotNull(res.authToken());
        assertFalse(res.authToken().isBlank());

        // token actually stored
        AuthData stored = authDAO.getAuth(res.authToken());
        assertNotNull(stored);
        assertEquals(res.authToken(), stored.authToken());
        assertEquals("kevin", stored.username());
    }

    @Test
    void login_wrongPassword_throws401() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.login(new LoginRequest("kevin", "nope")));

        assertEquals(401, ex.getStatus());
    }

    @Test
    void login_unknownUser_throws401() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.login(new LoginRequest("ghost", "pw123")));

        assertEquals(401, ex.getStatus());
    }

    @Test
    void login_blankUsername_throws400() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.login(new LoginRequest("   ", "pw123")));

        assertEquals(400, ex.getStatus());
    }
}
