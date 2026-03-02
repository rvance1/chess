package handler;

import com.google.gson.Gson;

import dto.CreateGameRequest;
import dto.CreateGameResult;
import dto.JoinGameRequest;
import dto.ListGamesResult;
import io.javalin.http.Context;
import service.GameService;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        String token = ctx.header("authorization");
        if (token == null) {
            token = ctx.header("Authorization");
        }

        ListGamesResult res = gameService.listGames(token);

        ctx.status(200);
        ctx.json(res);
    }
    
    public void createGame(Context ctx) {
        String token = ctx.header("authorization");
        if (token == null) {
            token = ctx.header("Authorization");
        }

        CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);

        CreateGameResult res = gameService.createGame(token, req);

        ctx.status(200);
        ctx.json(res);
    }

    public void joinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);

        gameService.joinGame(authToken, req);

        ctx.status(200);
        ctx.result("{}");
    }
}