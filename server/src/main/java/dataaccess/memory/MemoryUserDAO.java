package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.UserDAO;
import exception.DataAccessException;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) {
            throw new DataAccessException("user/username cannot be null");
        }
        if (users.containsKey(user.username())) {
            throw new DataAccessException("user already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("username cannot be null");
        }
        return users.get(username); // null if not found (normal)
    }
}