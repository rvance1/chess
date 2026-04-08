package server.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler implements Consumer<WsConfig> {
    private final Gson gson = new Gson();
    

    private final ConcurrentHashMap<Integer, Set<WsContext>> gameConnections = new ConcurrentHashMap<>();
    private final Set<Integer> finishedGames = ConcurrentHashMap.newKeySet();

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void accept(WsConfig ws) {
        
        ws.onConnect(ctx -> {
            System.out.println("New WebSocket connection: " + ctx.sessionId());
            ctx.session.setIdleTimeout(java.time.Duration.ofMinutes(5));
        });

        ws.onMessage(ctx -> {
            try {
                String message = ctx.message();
                System.out.println("Received message: " + message);
                
                UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
                
                switch (command.getCommandType()) {
                    case CONNECT -> connect(ctx, command);
                    case MAKE_MOVE -> {
                        MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                        makeMove(ctx, moveCommand);
                    }
                    case LEAVE -> leave(ctx, command);
                    case RESIGN -> resign(ctx, command);
                }
            } catch (Exception e) {
                ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
                ctx.send(gson.toJson(errorMessage));
            }
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket closed: " + ctx.sessionId());
            gameConnections.values().forEach(clients -> clients.remove(ctx));
        });

        ws.onError(ctx -> {
            System.err.println("WebSocket error: " + ctx.error());
        });
    }

    private void connect(WsContext ctx, UserGameCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) throw new Exception("unauthorized");
        
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) throw new Exception("game not found");

        gameConnections.computeIfAbsent(command.getGameID(), k -> ConcurrentHashMap.newKeySet()).add(ctx);

        LoadGameMessage loadMessage = new LoadGameMessage(gameData.game());
        ctx.send(gson.toJson(loadMessage));

        String role = "an observer";
        if (auth.username().equals(gameData.whiteUsername())) {
            role = "the white player";
        } else if (auth.username().equals(gameData.blackUsername())) {
            role = "the black player";
        }

        NotificationMessage notif = new NotificationMessage(auth.username() + " joined the game as " + role + ".");
        broadcastToOthers(command.getGameID(), ctx, notif);
    }

    private void makeMove(WsContext ctx, MakeMoveCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) throw new Exception("unauthorized");
        
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) throw new Exception("game not found");

        if (finishedGames.contains(command.getGameID())) {
            throw new Exception("game is already over");
        }

        String username = auth.username();
        ChessGame game = gameData.game();


        if (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !username.equals(gameData.whiteUsername())) {
            throw new Exception("not your turn");
        }
        if (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !username.equals(gameData.blackUsername())) {
            throw new Exception("not your turn");
        }

        ChessMove move = command.getMove();
        game.makeMove(move);


        gameDAO.updateGame(gameData);


        LoadGameMessage loadMessage = new LoadGameMessage(game);
        broadcastToAll(command.getGameID(), loadMessage);

        NotificationMessage notif = new NotificationMessage(username + " made a move.");
        broadcastToOthers(command.getGameID(), ctx, notif);

        if (game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            finishedGames.add(command.getGameID());
            broadcastToAll(command.getGameID(), new NotificationMessage("Checkmate!"));
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            finishedGames.add(command.getGameID());
            broadcastToAll(command.getGameID(), new NotificationMessage("Stalemate!"));
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)) {
            broadcastToAll(command.getGameID(), new NotificationMessage("Check!"));
        }
    }

    private void leave(WsContext ctx, UserGameCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) throw new Exception("unauthorized");
        
        GameData gameData = gameDAO.getGame(command.getGameID());
        
        if (gameData != null) {
            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();
            
            // Remove the user from their seat if they were a player
            if (auth.username().equals(white)) {
                white = null;
            } else if (auth.username().equals(black)) {
                black = null;
            }
            
            gameDAO.updateGame(new GameData(gameData.gameID(), white, black, gameData.gameName(), gameData.game()));
        }

        if (gameConnections.containsKey(command.getGameID())) {
            gameConnections.get(command.getGameID()).remove(ctx);
        }

        NotificationMessage notif = new NotificationMessage(auth.username() + " has left the game.");
        broadcastToOthers(command.getGameID(), ctx, notif);
    }

    private void resign(WsContext ctx, UserGameCommand command) throws Exception {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        if (auth == null) throw new Exception("unauthorized");
        
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) throw new Exception("game not found");

        if (!auth.username().equals(gameData.whiteUsername()) && !auth.username().equals(gameData.blackUsername())) {
            throw new Exception("observers cannot resign");
        }

        if (finishedGames.contains(command.getGameID())) {
            throw new Exception("game is already over");
        }

        finishedGames.add(command.getGameID());

        NotificationMessage notif = new NotificationMessage(auth.username() + " has resigned.");
        broadcastToAll(command.getGameID(), notif);
    }
    
    private void broadcastToAll(int gameID, Object messageObj) {
        Set<WsContext> clients = gameConnections.get(gameID);
        if (clients != null) {
            String jsonMessage = gson.toJson(messageObj);
            for (WsContext client : clients) {
                client.send(jsonMessage);
            }
        }
    }

    private void broadcastToOthers(int gameID, WsContext excludeCtx, Object messageObj) {
        Set<WsContext> clients = gameConnections.get(gameID);
        if (clients != null) {
            String jsonMessage = gson.toJson(messageObj);
            for (WsContext client : clients) {
                if (!client.equals(excludeCtx)) {
                    client.send(jsonMessage);
                }
            }
        }
    }
}