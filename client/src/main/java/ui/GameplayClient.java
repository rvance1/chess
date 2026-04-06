package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebSocketFacade;
import model.AuthData;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayClient {
    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;

    private final AuthData authData;
    private final GameData gameData;
    private final String playerColor; // "WHITE", "BLACK", or null for observer

    private ChessGame game;
    private boolean leftGame = false;

    public GameplayClient(ServerFacade serverFacade, AuthData authData, GameData gameData, String playerColor) throws Exception {
        this.serverFacade = serverFacade;
        this.authData = authData;
        this.gameData = gameData;
        this.playerColor = playerColor;

        this.webSocketFacade = new WebSocketFacade(this);

        connect();
    }

    public String eval(String input) throws Exception {
        String trimmed = input.trim().toLowerCase();

        if (trimmed.equals("help")) {
            return help();
        }
        if (trimmed.equals("redraw")) {
            return redrawBoard();
        }
        if (trimmed.equals("leave")) {
            return leave();
        }
        if (trimmed.equals("resign")) {
            return resign();
        }
        if (trimmed.startsWith("move")) {
            return makeMove(input);
        }
        if (trimmed.startsWith("highlight")) {
            return highlightMoves(input);
        }

        return "Unknown command. Type help.";
    }

    public String help() {
        return """
                help - show commands
                redraw
                leave
                resign
                move <start> <end>
                highlight <square>
                """;
    }

    private void connect() throws Exception {
        webSocketFacade.connect(authData.authToken(), gameData.gameID());
    }

    private String redrawBoard() {
        if (game == null) {
            return "Game has not loaded yet.";
        }

        boolean blackPerspective = "BLACK".equals(playerColor);
        BoardPrinter.drawBoard(game, blackPerspective, null);
        return "";
    }

    private String leave() throws Exception {
        webSocketFacade.leave(authData.authToken(), gameData.gameID());
        leftGame = true;
        return "Left game";
    }

    private String resign() throws Exception {
        if (playerColor == null) {
            return "Observers cannot resign.";
        }

        webSocketFacade.resign(authData.authToken(), gameData.gameID());
        return "Resigned game";
    }

    private String makeMove(String input) throws Exception {
        if (playerColor == null) {
            return "Observers cannot make moves.";
        }

        String[] tokens = input.split("\\s+");
        if (tokens.length != 3) {
            return "Usage: move <start> <end>";
        }

        ChessPosition start = parsePosition(tokens[1]);
        ChessPosition end = parsePosition(tokens[2]);

        ChessMove move = new ChessMove(start, end, null);
        webSocketFacade.makeMove(authData.authToken(), gameData.gameID(), move);

        return "";
    }

    private String highlightMoves(String input) {
        if (game == null) {
            return "Game has not loaded yet.";
        }

        String[] tokens = input.split("\\s+");
        if (tokens.length != 2) {
            return "Usage: highlight <square>";
        }

        ChessPosition position = parsePosition(tokens[1]);

        boolean blackPerspective = "BLACK".equals(playerColor);
        BoardPrinter.drawBoard(game, blackPerspective, position);

        return "";
    }

    private ChessPosition parsePosition(String square) {
        if (square.length() != 2) {
            throw new IllegalArgumentException("Invalid square");
        }

        char file = Character.toLowerCase(square.charAt(0));
        char rank = square.charAt(1);

        int col = file - 'a' + 1;
        int row = rank - '1' + 1;

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid square");
        }

        return new ChessPosition(row, col);
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.game = loadGameMessage.getGame();
                redrawBoard();
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(notificationMessage.getMessage());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(errorMessage.getErrorMessage());
            }
        }
    }

    public boolean hasLeftGame() {
        return leftGame;
    }
}