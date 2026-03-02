package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import exception.DataAccessException;
import exception.ServiceException;
import model.AuthData;
import model.GameData;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req) {
        // validate
        if (authToken == null || authToken.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        final AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }

        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        if (req == null || req.gameName() == null || req.gameName().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData gameData = new GameData(0, null, null, req.gameName(), new ChessGame());

        try {
            int gameID = gameDAO.createGame(gameData);
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }
}