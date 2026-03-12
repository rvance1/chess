package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.sql.MySqlAuthDAO;
import dataaccess.sql.MySqlUserDAO;
import exception.DataAccessException;
import model.AuthData;
import model.UserData;

public class MySqlAuthDAOTest {

    private MySqlAuthDAO authDAO;
    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new MySqlAuthDAO();
        userDAO = new MySqlUserDAO();

        authDAO.clear();
        userDAO.clear();

        userDAO.insertUser(new UserData("bob", "password", "bob@email.com"));
    }

    // clear() positive
    @Test
    public void clearPositive() throws DataAccessException {

        AuthData auth = new AuthData("token123", "bob");
        authDAO.createAuth(auth);

        authDAO.clear();

        AuthData result = authDAO.getAuth("token123");
        assertNull(result);
    }

    // createAuth positive
    @Test
    public void createAuthPositive() throws DataAccessException {

        AuthData auth = new AuthData("token123", "bob");

        authDAO.createAuth(auth);

        AuthData result = authDAO.getAuth("token123");

        assertNotNull(result);
        assertEquals("token123", result.authToken());
        assertEquals("bob", result.username());
    }

    // createAuth negative (duplicate token)
    @Test
    public void createAuthNegative() throws DataAccessException {

        AuthData auth = new AuthData("token123", "bob");

        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(auth);
        });
    }

    // getAuth positive
    @Test
    public void getAuthPositive() throws DataAccessException {

        AuthData auth = new AuthData("token123", "bob");
        authDAO.createAuth(auth);

        AuthData result = authDAO.getAuth("token123");

        assertNotNull(result);
        assertEquals("bob", result.username());
    }

    // getAuth negative
    @Test
    public void getAuthNegative() throws DataAccessException {

        AuthData result = authDAO.getAuth("notAToken");

        assertNull(result);
    }

    // deleteAuth positive
    @Test
    public void deleteAuthPositive() throws DataAccessException {

        AuthData auth = new AuthData("token123", "bob");
        authDAO.createAuth(auth);

        authDAO.deleteAuth("token123");

        AuthData result = authDAO.getAuth("token123");

        assertNull(result);
    }

    // deleteAuth negative
    @Test
    public void deleteAuthNegative() throws DataAccessException {

        assertDoesNotThrow(() -> {
            authDAO.deleteAuth("notAToken");
        });
    }
}