package handler;

import handler.results.RegisterResult;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            UserData req = ctx.bodyAsClass(UserData.class); // expects username/password/email
            RegisterResult res = userService.register(req);

            ctx.status(200).json(res);

        } catch (BadRequestException e) {
            ctx.status(400).json(new ErrorResult(e.getMessage()));
        } catch (AlreadyTakenException e) {
            ctx.status(403).json(new ErrorResult(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}