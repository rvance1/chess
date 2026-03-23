package client;

import chess.ChessGame;
import chess.ChessPiece;
import ui.REPL;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        REPL repl = new REPL("http://localhost:8080");
        repl.run();
    }
}
