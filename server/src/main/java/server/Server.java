package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.ErrorMessage;
import exception.ServiceException;
import handler.ClearHandler;
import handler.GameHandler;
import handler.SessionHandler;
import handler.UserHandler;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson()); // <-- add this
        });

        //error mapping
        javalin.exception(ServiceException.class, (e, ctx) -> {
            ctx.status(e.getStatus());
            ctx.json(new ErrorMessage(e.getMessage()));
        });

        //DAOs
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        //Services
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        //Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        UserHandler userHandler = new UserHandler(userService);
        SessionHandler sessionHandler = new SessionHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        //Routes
        javalin.delete("/db", clearHandler::handle);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);
        javalin.post("/game", gameHandler::createGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
