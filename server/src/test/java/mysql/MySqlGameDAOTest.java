package mysql;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.sql.MySqlGameDAO;
import dataaccess.sql.MySqlUserDAO;
import exception.DataAccessException;
import model.GameData;
import model.UserData;

public class MySqlGameDAOTest {

    private MySqlGameDAO gameDAO;
    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
        userDAO = new MySqlUserDAO();

        gameDAO.clear();
        userDAO.clear();

        userDAO.insertUser(new UserData("white", "password", "white@email.com"));
        userDAO.insertUser(new UserData("black", "password", "black@email.com"));
    }

    @Test
    public void clearPositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "white", "black", "testGame", chessGame);

        int gameID = gameDAO.createGame(game);
        assertNotNull(gameDAO.getGame(gameID));

        gameDAO.clear();

        assertNull(gameDAO.getGame(gameID));
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "white", "black", "testGame", chessGame);

        int gameID = gameDAO.createGame(game);
        GameData result = gameDAO.getGame(gameID);

        assertNotNull(result);
        assertEquals(gameID, result.gameID());
        assertEquals("white", result.whiteUsername());
        assertEquals("black", result.blackUsername());
        assertEquals("testGame", result.gameName());
        assertNotNull(result.game());
    }

    @Test
    public void createGameNegative() {
        ChessGame chessGame = new ChessGame();
        GameData badGame = new GameData(0, "notAUser", "black", "badGame", chessGame);

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(badGame));
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "white", "black", "testGame", chessGame);

        int gameID = gameDAO.createGame(game);
        GameData result = gameDAO.getGame(gameID);

        assertNotNull(result);
        assertEquals(gameID, result.gameID());
        assertEquals("testGame", result.gameName());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData result = gameDAO.getGame(999999);
        assertNull(result);
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();

        gameDAO.createGame(new GameData(0, "white", "black", "game1", game1));
        gameDAO.createGame(new GameData(0, "white", null, "game2", game2));

        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "white", null, "testGame", chessGame);

        int gameID = gameDAO.createGame(game);

        GameData updatedGame = new GameData(gameID, "white", "black", "updatedName", chessGame);
        gameDAO.updateGame(updatedGame);

        GameData result = gameDAO.getGame(gameID);

        assertNotNull(result);
        assertEquals("black", result.blackUsername());
        assertEquals("updatedName", result.gameName());
    }

    @Test
    public void updateGameNegative() {
        ChessGame chessGame = new ChessGame();
        GameData fakeGame = new GameData(999999, "white", "black", "fakeGame", chessGame);

        assertDoesNotThrow(() -> gameDAO.updateGame(fakeGame));
    }
}