package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.JoinGameRequest;
import exception.DataAccessException;
import exception.ServiceException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class GameServiceJoinTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userDAO.clear(); 
        authDAO.clear(); 
        gameDAO.clear();

        gameService = new GameService(authDAO, gameDAO);
    }

    private void makeUserAndToken(String username, String token) throws DataAccessException {
        userDAO.insertUser(new UserData(username, "pw", "e@e.com"));
        authDAO.createAuth(new AuthData(token, username));
    }

    private int makeGame(String name) throws DataAccessException {
        GameData newGame = new GameData(0, null, null, name, new ChessGame());
        return gameDAO.createGame(newGame);
    }

    private void setWhite(int gameID, String username) throws DataAccessException {
        GameData g = gameDAO.getGame(gameID);
        gameDAO.updateGame(new GameData(g.gameID(), username, g.blackUsername(), g.gameName(), g.game()));
    }

    @Test
    void joinGame_claimWhite_success() throws Exception {
        makeUserAndToken("u1", "t1");
        int gameID = makeGame("g1");

        gameService.joinGame("t1", new JoinGameRequest("WHITE", gameID));

        GameData g = gameDAO.getGame(gameID);
        assertEquals("u1", g.whiteUsername());
    }

    @Test
    void joinGame_whiteAlreadyTaken_throws403() throws Exception {
        makeUserAndToken("u1", "t1");
        makeUserAndToken("u2", "t2");
        int gameID = makeGame("g1");

        setWhite(gameID, "u1"); // pre-fill white seat

        ServiceException ex = assertThrows(ServiceException.class,
                () -> gameService.joinGame("t2", new JoinGameRequest("WHITE", gameID)));

        assertEquals(403, ex.getStatus());
    }
}