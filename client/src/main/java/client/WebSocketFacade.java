package client;

import chess.ChessMove;
import com.google.gson.Gson;
import ui.GameplayClient;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {
    private final GameplayClient gameplayClient;
    private Session session;
    private final Gson gson = new Gson();

    public WebSocketFacade(GameplayClient gameplayClient) throws Exception {
        this.gameplayClient = gameplayClient;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String url = "ws://localhost:8080/ws"; // replace if needed
        this.session = container.connectToServer(this, URI.create(url));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;

        session.addMessageHandler(String.class, message -> {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            gameplayClient.notify(serverMessage);
        });
    }

    public void connect(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        send(command);
    }

    public void leave(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        send(command);
    }

    public void resign(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        send(command);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        send(command);
    }

    private void send(Object command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }
}