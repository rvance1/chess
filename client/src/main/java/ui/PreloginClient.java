package ui;

import client.ServerFacade;
import model.AuthData;

public class PreloginClient {
    private final ServerFacade serverFacade;
    private AuthData authData;
    private boolean quit = false;

    public PreloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String eval(String input) throws Exception {
        var tokens = input.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isBlank()) {
            return help();
        }

        String command = tokens[0].toLowerCase();

        return switch (command) {
            case "help" -> help();
            case "quit" -> quit();
            case "login" -> login(tokens);
            case "register" -> register(tokens);
            default -> "Unknown command. Type help.";
        };
    }

    public String help() {
        return """
                help - show commands
                register <username> <password> <email>
                login <username> <password>
                quit - exit
                """;
    }

    private String quit() {
        quit = true;
        return "Goodbye";
    }

    private String login(String[] tokens) throws Exception {
        if (tokens.length != 3) {
            return "Usage: login <username> <password>";
        }

        authData = serverFacade.login(tokens[1], tokens[2]);
        return "Logged in as " + authData.username();
    }

    private String register(String[] tokens) throws Exception {
        if (tokens.length != 4) {
            return "Usage: register <username> <password> <email>";
        }

        authData = serverFacade.register(tokens[1], tokens[2], tokens[3]);
        return "Registered and logged in as " + authData.username();
    }

    public boolean isLoggedIn() {
        return authData != null;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public void clearAuth() {
        authData = null;
    }

    public boolean shouldQuit() {
        return quit;
    }
}