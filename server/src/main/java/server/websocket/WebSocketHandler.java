package server.websocket;

import java.util.function.Consumer;

import com.google.gson.Gson;

import io.javalin.websocket.WsConfig;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements Consumer<WsConfig> {
    private final Gson gson = new Gson();

    @Override
    public void accept(WsConfig ws) {
        
        ws.onConnect(ctx -> {
            System.out.println("New WebSocket connection: " + ctx.sessionId());
        });

        ws.onMessage(ctx -> {
            String message = ctx.message();
            System.out.println("Received message: " + message);
            
            // Deserialize the message to figure out what the client wants
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            
            // TODO: Route to the appropriate method (CONNECT, MAKE_MOVE, LEAVE, RESIGN) based on command.getCommandType()
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket closed: " + ctx.sessionId());
            // TODO: Remove the user from your active games tracking
        });

        ws.onError(ctx -> {
            System.err.println("WebSocket error: " + ctx.error());
        });
    }
}