package ui;

import java.util.Scanner;

import client.ServerFacade;

public class REPL {
    private final ServerFacade serverFacade;
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private boolean loggedIn = false;
    private boolean running = true;

    public REPL(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.preloginClient = new PreloginClient(serverFacade);
        this.postloginClient = new PostloginClient(serverFacade);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess");
        System.out.println(preloginClient.help());

        while (running) {
            System.out.print(loggedIn ? "[LOGGED_IN] >>> " : "[LOGGED_OUT] >>> ");
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
                } else {
                    String result = postloginClient.eval(input);
                    System.out.println(result);

                    if (postloginClient.isLoggedOut()) {
                        loggedIn = false;
                        preloginClient.clearAuth();
                    }

                    if (postloginClient.shouldQuit()) {
                        running = false;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        scanner.close();
    }
}