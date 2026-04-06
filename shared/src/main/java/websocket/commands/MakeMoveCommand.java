package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        // Call the parent constructor with the MAKE_MOVE command type
        super(CommandType.MAKE_MOVE, authToken, gameID); 
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}