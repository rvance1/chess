package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;

    // you’ll need these next, so define them now
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}