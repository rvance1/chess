package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> tokens = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        tokens.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.authToken() == null || auth.username() == null) {
            throw new DataAccessException("authToken/username cannot be null");
        }
        if (tokens.containsKey(auth.authToken())) {
            throw new DataAccessException("auth token already exists");
        }
        tokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("authToken cannot be null");
        }
        return tokens.get(authToken); // null if not found
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("authToken cannot be null");
        }
        tokens.remove(authToken); // ok even if absent
    }
}