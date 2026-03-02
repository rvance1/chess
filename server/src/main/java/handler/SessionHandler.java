package handler;

import com.google.gson.Gson;

import dto.LoginRequest;
import dto.LoginResult;
import io.javalin.http.Context;
import service.UserService;

public class SessionHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public SessionHandler(UserService userService) {
        this.userService = userService;
    }

    public void login(Context ctx) {
        // body
        LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);

        // svc
        LoginResult res = userService.login(req);

        ctx.status(200);
        ctx.json(res);
    }

    public void logout(Context ctx) {
        // header
        String token = ctx.header("Authorization");

        // svc
        userService.logout(token);

        ctx.status(200);
        ctx.result("{}");
    }
}