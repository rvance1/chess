package model;

public class UserGameCommand {
    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType commandType;
    public String authToken;
    public Integer gameID;
}