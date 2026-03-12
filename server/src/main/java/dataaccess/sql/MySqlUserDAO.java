package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import exception.DataAccessException;
import model.UserData;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}