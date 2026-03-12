package mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.sql.MySqlUserDAO;
import exception.DataAccessException;
import model.UserData;

public class MySqlUserDAOTest {

    MySqlUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    public void insertUserPositive() throws DataAccessException {
        UserData user = new UserData("bob", "password", "bob@email.com");

        userDAO.insertUser(user);
        UserData result = userDAO.getUser("bob");

        assertNotNull(result);
        assertEquals("bob", result.username());
        assertEquals("bob@email.com", result.email());
    }

    @Test
    public void insertUserNegative() throws DataAccessException {
        UserData user = new UserData("bob", "password", "bob@email.com");

        userDAO.insertUser(user);

        assertThrows(DataAccessException.class, () -> {
            userDAO.insertUser(user);
        });
    }
}