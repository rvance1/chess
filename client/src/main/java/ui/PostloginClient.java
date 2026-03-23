package ui;

import client.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostloginClient {
    private final ServerFacade serverFacade;
    private AuthData authData;
    private boolean loggedOut = false;
    private boolean quit = false;
    private List<GameData> lastListedGames = new ArrayList<>();

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void setAuth(AuthData authData) {
        this.authData = authData;
        this.loggedOut = false;
    }

    public String eval(String input) throws Exception {
        String trimmed = input.trim().toLowerCase();

        if (trimmed.equals("help")) {
            return help();
        }
        if (trimmed.equals("logout")) {
            return logout();
        }
        if (trimmed.equals("quit")) {
            return quit();
        }

        if (trimmed.startsWith("create game")) {
            return createGame(input);
        }
        if (trimmed.equals("list games")) {
            return listGames();
        }
        if (trimmed.startsWith("play game")) {
            return playGame(input);
        }
        if (trimmed.startsWith("observe game")) {
            return observeGame(input);
        }

        return "Unknown command. Type help.";
    }

    public String help() {
        return """
                help - show commands
                create game <name>
                list games
                play game <listNumber> <WHITE|BLACK>
                observe game <listNumber>
                logout
                quit
                """;
    }

    private String logout() throws Exception {
        serverFacade.logout(authData.authToken());
        authData = null;
        loggedOut = true;
        return "Logged out";
    }

    private String quit() {
        quit = true;
        return "Goodbye";
    }

    private String createGame(String input) throws Exception {
        String name = input.substring("create game".length()).trim();
        if (name.isBlank()) {
            return "Usage: create game <name>";
        }

        int gameId = serverFacade.createGame(authData.authToken(), name);
        return "Created game " + name + " (id " + gameId + ")";
    }

    private String listGames() throws Exception {
        Collection<GameData> games = serverFacade.listGames(authData.authToken());
        lastListedGames = new ArrayList<>(games);

        if (lastListedGames.isEmpty()) {
            return "No games found";
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lastListedGames.size(); i++) {
            GameData game = lastListedGames.get(i);
            out.append(i + 1).append(". ")
               .append(game.gameName())
               .append(" | white: ").append(game.whiteUsername())
               .append(" | black: ").append(game.blackUsername())
               .append("\n");
        }
        return out.toString();
    }

    private String playGame(String input) throws Exception {
        String[] tokens = input.split("\\s+");
        if (tokens.length < 4) {
            return "Usage: play game <listNumber> <WHITE|BLACK>";
        }

        int listNumber = Integer.parseInt(tokens[2]);
        String color = tokens[3].toUpperCase();

        GameData game = lastListedGames.get(listNumber - 1);
        serverFacade.joinGame(authData.authToken(), color, game.gameID());

        BoardPrinter.drawBoard(color.equals("BLACK"));
        return "Joined game " + game.gameName() + " as " + color;
    }

    private String observeGame(String input) {
        String[] tokens = input.split("\\s+");
        if (tokens.length < 3) {
            return "Usage: observe game <listNumber>";
        }

        int listNumber = Integer.parseInt(tokens[2]);
        GameData game = lastListedGames.get(listNumber - 1);

        BoardPrinter.drawBoard(false); // observer uses white perspective
        return "Observing game " + game.gameName();
    }

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public boolean shouldQuit() {
        return quit;
    }
}