package service;

import java.util.List;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import dto.JoinGameRequest;
import dto.ListGamesResult;
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

    public ListGamesResult listGames(String authToken) {
        // 1) auth required
        if (authToken == null || authToken.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        try {
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            // 2) read all games
            List<GameData> items = gameDAO.listGames().stream().toList();

            // 3) return in expected wrapper
            return new ListGamesResult(items);

        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
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

    public void joinGame(String authToken, JoinGameRequest req) {

        if (authToken == null || authToken.isBlank() || req == null || req.gameID() == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (req.playerColor() == null || req.playerColor().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        String color = req.playerColor().trim().toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            // auth
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException(401, "Error: unauthorized");
            }
            String username = auth.username();

            // game exists
            GameData game = gameDAO.getGame(req.gameID());
            if (game == null) {
                throw new ServiceException(400, "Error: bad request");
            }

            // join rules
            String white = game.whiteUsername();
            String black = game.blackUsername();

            // Seat availability
            if (color.equals("WHITE")) {
                if (white != null && !white.equals(username)) {
                    throw new ServiceException(403, "Error: already taken");
                }
                white = username;
            } else { // BLACK
                if (black != null && !black.equals(username)) {
                    throw new ServiceException(403, "Error: already taken");
                }
                black = username;
            }

            // claim
            GameData updated = new GameData(game.gameID(), white, black, game.gameName(), game.game());
            gameDAO.updateGame(updated);

        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }
}