package service;

import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import handler.results.RegisterResult;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(UserData req) throws Exception {
        if (req == null ||
            isBlank(req.username()) ||
            isBlank(req.password()) ||
            isBlank(req.email())) {
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(req.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        userDAO.insertUser(req);

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, req.username()));

        return new RegisterResult(req.username(), token);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}