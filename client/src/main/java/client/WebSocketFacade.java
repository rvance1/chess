package client;

import java.net.URI;

import com.google.gson.Gson;

import chess.ChessMove;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import ui.GameplayClient;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage genericMessage = gson.fromJson(message, ServerMessage.class);

                switch (genericMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        LoadGameMessage loadMessage = gson.fromJson(message, LoadGameMessage.class);
                        gameplayClient.notify(loadMessage);
                    }
                    case ERROR -> {
                        ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                        gameplayClient.notify(errorMessage);
                    }
                    case NOTIFICATION -> {
                        NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                        gameplayClient.notify(notificationMessage);
                    }
                }
            }
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