package handler;

import exception.AlreadyTakenException;
import exception.BadRequestException;
import handler.results.ErrorResult;
import handler.results.RegisterResult;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            UserData req = ctx.bodyAsClass(UserData.class); 
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