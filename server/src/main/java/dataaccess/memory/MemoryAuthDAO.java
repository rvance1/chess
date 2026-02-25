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

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}