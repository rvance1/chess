package dataaccess.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import dataaccess.GameDAO;
import exception.DataAccessException;
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
        if (game == null) {
            throw new DataAccessException("game cannot be null");
        }

        int id = nextId.getAndIncrement();

        GameData stored = new GameData(
                id,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );

        games.put(id, stored);
        return id;
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
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("game cannot be null");
        }
        int id = game.gameID();
        if (!games.containsKey(id)) {
            throw new DataAccessException("game does not exist");
        }
        games.put(id, game);
    }
}