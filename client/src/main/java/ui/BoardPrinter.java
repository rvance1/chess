package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
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

    public static void drawBoard(boolean blackPerspective) {
        if (blackPerspective) {
            drawBlackBoard();
        } else {
            drawWhiteBoard();
        }
    }

    private static void drawWhiteBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawFileLabels(out, false);

        for (int row = 8; row >= 1; row--) {
            drawBoardRow(out, row, false);
        }

        drawFileLabels(out, false);
        resetColors(out);
    }

    private static void drawBlackBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawFileLabels(out, true);

        for (int row = 1; row <= 8; row++) {
            drawBoardRow(out, row, true);
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

    private static void drawBoardRow(PrintStream out, int row, boolean blackPerspective) {
        setLabelColors(out);
        out.print(" " + row + " ");
        resetColors(out);

        if (blackPerspective) {
            for (int col = 8; col >= 1; col--) {
                drawSquare(out, row, col);
            }
        } else {
            for (int col = 1; col <= 8; col++) {
                drawSquare(out, row, col);
            }
        }

        setLabelColors(out);
        out.println(" " + row + " ");
        resetColors(out);
    }

    private static void drawSquare(PrintStream out, int row, int col) {
        boolean lightSquare = ((row + col) % 2 == 1);

        if (lightSquare) {
            out.print(SET_BG_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN);
        }

        String piece = getStartingPiece(row, col);

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

        private static String getStartingPiece(int row, int col) {
            if (row == 2) {
                return WHITE_PAWN;
            }
            if (row == 7) {
                return BLACK_PAWN;
            }
            if (row == 1) {
                return getBackRankPiece(col, true);
            }
            if (row == 8) {
                return getBackRankPiece(col, false);
            }
            return EMPTY;
        }

        private static String getBackRankPiece(int col, boolean white) {
            return switch (col) {
                case 1, 8 -> white ? WHITE_ROOK : BLACK_ROOK;
                case 2, 7 -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
                case 3, 6 -> white ? WHITE_BISHOP : BLACK_BISHOP;
                case 4 -> white ? WHITE_QUEEN : BLACK_QUEEN;
                case 5 -> white ? WHITE_KING : BLACK_KING;
                default -> EMPTY;
            };
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