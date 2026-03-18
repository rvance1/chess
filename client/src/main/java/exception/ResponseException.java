package exception;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ResponseException extends Exception {

    public enum Code {
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        SERVER_ERROR
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public Code code() {
        return code;
    }

    public static Code fromHttpStatusCode(int statusCode) {
        return switch (statusCode) {
            case 400 -> Code.BAD_REQUEST;
            case 401 -> Code.UNAUTHORIZED;
            case 403 -> Code.FORBIDDEN;
            case 500 -> Code.SERVER_ERROR;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + statusCode);
        };
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case BAD_REQUEST -> 400;
            case UNAUTHORIZED -> 401;
            case FORBIDDEN -> 403;
            case SERVER_ERROR -> 500;
        };
    }
}