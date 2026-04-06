package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.BLACK_BISHOP;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_KNIGHT;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_QUEEN;
import static ui.EscapeSequences.BLACK_ROOK;
import static ui.EscapeSequences.EMPTY;
import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_BG_COLOR_YELLOW;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;
import static ui.EscapeSequences.WHITE_BISHOP;
import static ui.EscapeSequences.WHITE_KING;
import static ui.EscapeSequences.WHITE_KNIGHT;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_QUEEN;
import static ui.EscapeSequences.WHITE_ROOK;

public class BoardPrinter {

    public static void drawBoard(ChessGame game, boolean blackPerspective) {
        if (blackPerspective) {
            drawBlackBoard(game, null);
        } else {
            drawWhiteBoard(game, null);
        }
    }

    private static void drawWhiteBoard(ChessGame game, ChessPosition highlightedPosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawFileLabels(out, false);

        for (int row = 8; row >= 1; row--) {
            drawBoardRow(out, row, false, game, highlightedPosition);
        }

        drawFileLabels(out, false);
        resetColors(out);
    }

    private static void drawBlackBoard(ChessGame game, ChessPosition highlightedPosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawFileLabels(out, true);

        for (int row = 1; row <= 8; row++) {
            drawBoardRow(out, row, true, game, highlightedPosition);
        }

        drawFileLabels(out, true);
        resetColors(out);
    }

    private static void drawFileLabels(PrintStream out, boolean blackPerspective) {
        setLabelColors(out);
        out.print("   ");

        if (blackPerspective) {
            for (char file = 'h'; file >= 'a'; file--) {
                out.print(" " + file + " ");
            }
        } else {
            for (char file = 'a'; file <= 'h'; file++) {
                out.print(" " + file + " ");
            }
        }

        out.println();
        resetColors(out);
    }

    private static void drawBoardRow(PrintStream out, int row, boolean blackPerspective, ChessGame game, ChessPosition highlightedPosition) {
        setLabelColors(out);
        out.print(" " + row + " ");
        resetColors(out);

        if (blackPerspective) {
            for (int col = 8; col >= 1; col--) {
                drawSquare(out, row, col, game, highlightedPosition);
            }
        } else {
            for (int col = 1; col <= 8; col++) {
                drawSquare(out, row, col, game, highlightedPosition);
            }
        }

        setLabelColors(out);
        out.println(" " + row + " ");
        resetColors(out);
    }

    private static void drawSquare(PrintStream out, int row, int col, ChessGame game, ChessPosition highlightedPosition) {
        ChessPosition currentPosition = new ChessPosition(row, col);

        boolean isHighlightedStart = highlightedPosition != null
                && highlightedPosition.getRow() == row
                && highlightedPosition.getColumn() == col;

        boolean isHighlightedMove = false;
        if (highlightedPosition != null) {
            var legalMoves = game.validMoves(highlightedPosition);
            if (legalMoves != null) {
                for (ChessMove move : legalMoves) {
                    ChessPosition end = move.getEndPosition();
                    if (end.getRow() == row && end.getColumn() == col) {
                        isHighlightedMove = true;
                        break;
                    }
                }
            }
        }

        if (isHighlightedStart) {
            out.print(SET_BG_COLOR_YELLOW);
        } else if (isHighlightedMove) {
            out.print(SET_BG_COLOR_GREEN);
        } else {
            boolean lightSquare = ((row + col) % 2 == 1);
            if (lightSquare) {
                out.print(SET_BG_COLOR_WHITE);
            } else {
                out.print(SET_BG_COLOR_DARK_GREEN);
            }
        }

        ChessPiece pieceObj = game.getBoard().getPiece(currentPosition);
        String piece = getPieceString(pieceObj);

        if (piece.equals(WHITE_KING) || piece.equals(WHITE_QUEEN) || piece.equals(WHITE_BISHOP)
                || piece.equals(WHITE_KNIGHT) || piece.equals(WHITE_ROOK) || piece.equals(WHITE_PAWN)) {
            out.print(SET_TEXT_COLOR_RED);
        } else if (piece.equals(BLACK_KING) || piece.equals(BLACK_QUEEN) || piece.equals(BLACK_BISHOP)
                || piece.equals(BLACK_KNIGHT) || piece.equals(BLACK_ROOK) || piece.equals(BLACK_PAWN)) {
            out.print(SET_TEXT_COLOR_BLUE);
        }

        out.print(piece);
        resetColors(out);
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        }
    }

    private static void setLabelColors(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void resetColors(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
}