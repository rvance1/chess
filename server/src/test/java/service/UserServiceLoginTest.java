package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.LoginRequest;
import dto.LoginResult;
import exception.DataAccessException;
import exception.ServiceException;
import model.AuthData;
import model.UserData;

public class UserServiceLoginTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        // user in db
        String hashedPassword = BCrypt.hashpw("pw123", BCrypt.gensalt());
        userDAO.insertUser(new UserData("kevin", hashedPassword, "k@x.com"));
    }

    @Test
    void loginSuccessReturnsTokenAndStoresAuth() throws DataAccessException {
        LoginResult res = userService.login(new LoginRequest("kevin", "pw123"));

        // returned data
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
    void loginWrongPasswordThrows401() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.login(new LoginRequest("kevin", "nope")));

        assertEquals(401, ex.getStatus());
    }

    @Test
    void loginUnknownUserThrows401() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.login(new LoginRequest("ghost", "pw123")));

        assertEquals(401, ex.getStatus());
    }

    @Test
    void loginBlankUsernameThrows400() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.login(new LoginRequest("   ", "pw123")));

        assertEquals(400, ex.getStatus());
    }
}
