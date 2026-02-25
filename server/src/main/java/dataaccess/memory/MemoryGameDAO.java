package dataaccess.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void clear() throws DataAccessException {
        games.clear();
        nextId.set(1);
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (gameID <= 0) {
            throw new DataAccessException("gameID must be positive");
        }
        return games.get(gameID); // null if not found
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values()); //get a copy
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }
}