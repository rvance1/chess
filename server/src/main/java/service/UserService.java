package service;

import java.util.Objects;
import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dto.LoginRequest;
import dto.LoginResult;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.DataAccessException;
import exception.ServiceException;
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

    public LoginResult login(LoginRequest req) {
        // basic req check
        if (req == null || isBlank(req.username()) || isBlank(req.password())) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            UserData user = userDAO.getUser(req.username());

            // no user
            if (user == null) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            // pw wrong
            if (!Objects.equals(req.password(), user.password())) {
                throw new ServiceException(401, "Error: unauthorized");
            }

            // new token per login
            String token = UUID.randomUUID().toString(); // quick token gen
            authDAO.createAuth(new AuthData(token, user.username()));

            return new LoginResult(user.username(), token);

        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }

    public void logout(String authToken) {
    // token check
    if (authToken == null || authToken.trim().isEmpty()) {
        throw new ServiceException(401, "Error: unauthorized");
    }

    try {
        AuthData auth = authDAO.getAuth(authToken);

        // token not found
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        // delete it
        authDAO.deleteAuth(authToken);

    } catch (DataAccessException e) {
        throw new ServiceException(500, "Error: " + e.getMessage());
    }
}

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}