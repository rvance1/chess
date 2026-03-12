package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.sql.MySqlUserDAO;
import exception.DataAccessException;
import model.UserData;

public class MySqlUserDAOTest {

    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    // clear() positive test
    @Test
    public void clearPositive() throws DataAccessException {

        UserData user = new UserData("bob", "password", "bob@email.com");
        userDAO.insertUser(user);

        userDAO.clear();

        UserData result = userDAO.getUser("bob");
        assertNull(result);
    }

    // insertUser positive
    @Test
    public void insertUserPositive() throws DataAccessException {

        UserData user = new UserData("bob", "password", "bob@email.com");

        userDAO.insertUser(user);
        UserData result = userDAO.getUser("bob");

        assertNotNull(result);
        assertEquals("bob", result.username());
        assertEquals("bob@email.com", result.email());
    }

    // insertUser negative
    @Test
    public void insertUserNegative() throws DataAccessException {

        UserData user = new UserData("bob", "password", "bob@email.com");

        userDAO.insertUser(user);

        assertThrows(DataAccessException.class, () -> {
            userDAO.insertUser(user);
        });
    }

    // getUser positive
    @Test
    public void getUserPositive() throws DataAccessException {

        UserData user = new UserData("bob", "password", "bob@email.com");
        userDAO.insertUser(user);

        UserData result = userDAO.getUser("bob");

        assertNotNull(result);
        assertEquals("bob", result.username());
    }

    // getUser negative
    @Test
    public void getUserNegative() throws DataAccessException {

        UserData result = userDAO.getUser("notAUser");

        assertNull(result);
    }
}