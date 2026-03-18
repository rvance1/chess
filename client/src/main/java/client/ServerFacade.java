package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;

import com.google.gson.Gson;

import dto.LoginRequest;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        UserData userData = new UserData(username, password, email);
        var request = buildRequest("POST", "/user", userData, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var loginRequest = new LoginRequest(username, password);
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + path))
                    .method(method, makeRequestBody(body));

            if (body != null) {
                request.setHeader("Content-Type", "application/json");
            }

            if (authToken != null) {
                request.setHeader("Authorization", authToken);
            }

            return request.build();
    }


    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }


    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();

        if (!isSuccessful(status)) {
            String message = "other failure: " + status;
            var body = response.body();

            if (body != null && !body.isBlank()) {
                try {
                    var map = new Gson().fromJson(body, java.util.HashMap.class);
                    var msg = map.get("message");
                    if (msg != null) {
                        message = msg.toString();
                    }
                } catch (Exception ignored) {
                }
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), message);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}