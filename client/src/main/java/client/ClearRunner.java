package client;
import exception.ResponseException;

public class ClearRunner {
    public static void main(String[] args) throws ResponseException {
        ServerFacade facade = new ServerFacade("http://localhost:8080");
        facade.clear();
        System.out.println("Database cleared.");
    }
}
