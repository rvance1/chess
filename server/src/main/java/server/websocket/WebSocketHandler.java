package server.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.gson.Gson;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

public class WebSocketHandler implements Consumer<WsConfig> {
    private final Gson gson = new Gson();
    
    private final ConcurrentHashMap<Integer, Set<WsContext>> gameConnections = new ConcurrentHashMap<>();

    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public void accept(WsConfig ws) {
        
        ws.onConnect(ctx -> {
            System.out.println("New WebSocket connection: " + ctx.sessionId());
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
        gameConnections.computeIfAbsent(command.getGameID(), k -> ConcurrentHashMap.newKeySet()).add(ctx);
    }

    private void makeMove(WsContext ctx, MakeMoveCommand command) throws Exception {
    }

    private void leave(WsContext ctx, UserGameCommand command) throws Exception {
        if (gameConnections.containsKey(command.getGameID())) {
            gameConnections.get(command.getGameID()).remove(ctx);
        }
    }

    private void resign(WsContext ctx, UserGameCommand command) throws Exception {
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