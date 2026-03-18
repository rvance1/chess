package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AuthData login(String username, String password) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void logout(String authToken) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}