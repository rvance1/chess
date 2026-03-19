package ui;

public class BoardPrinter {

    public static void drawBoard(boolean blackPerspective) {
        if (blackPerspective) {
            drawBlackBoard();
        } else {
            drawWhiteBoard();
        }
    }

    private static void drawWhiteBoard() {
        System.out.println("White perspective board here");
    }

    private static void drawBlackBoard() {
        System.out.println("Black perspective board here");
    }
}