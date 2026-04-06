package ui;

import java.util.Scanner;

import client.ServerFacade;
import model.AuthData;
import model.GameData;

public class REPL {
    private final ServerFacade serverFacade;
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private GameplayClient gameplayClient; // Removed 'final' so we can create it dynamically
    
    private boolean loggedIn = false;
    private boolean playing = false;
    private boolean running = true;

    public REPL(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.preloginClient = new PreloginClient(serverFacade);
        this.postloginClient = new PostloginClient(serverFacade);
        // Do not instantiate gameplayClient here; wait until the user joins a game.
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess");
        System.out.println(preloginClient.help());

        while (running) {
            // Dynamically change the prompt based on the user's state
            if (!loggedIn) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else if (!playing) {
                System.out.print("[LOGGED_IN] >>> ");
            } else {
                System.out.print("[GAMEPLAY] >>> ");
            }
            
            String input = scanner.nextLine();

            try {
                if (!loggedIn) {
                    String result = preloginClient.eval(input);
                    System.out.println(result);

                    if (preloginClient.isLoggedIn()) {
                        loggedIn = true;
                        postloginClient.setAuth(preloginClient.getAuthData());
                    }

                    if (preloginClient.shouldQuit()) {
                        running = false;
                    }
                } else if (!playing) {
                    String result = postloginClient.eval(input);
                    System.out.println(result);

                    if (postloginClient.isLoggedOut()) {
                        loggedIn = false;
                        preloginClient.clearAuth();
                    }

                    if (postloginClient.isJoiningGame()) {
                        playing = true;
                        
                        AuthData auth = postloginClient.getAuthData();
                        GameData game = postloginClient.getJoinedGame();
                        String color = postloginClient.getPlayerColor(); 

                        gameplayClient = new GameplayClient(serverFacade, auth, game, color);
                        
                        postloginClient.resetJoinState();
                    }

                    if (postloginClient.shouldQuit()) {
                        running = false;
                    }
                } else {
                    String result = gameplayClient.eval(input);
                    System.out.println(result);

                    if (gameplayClient.hasLeftGame()) {
                        playing = false;
                        gameplayClient = null;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        scanner.close();
    }
}