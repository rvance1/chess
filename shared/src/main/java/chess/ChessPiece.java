package chess;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) {
            int[][] myDirections = new int[][] {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            };
            
            return basicMoves(board, myPosition, myDirections, true);
        }
        
        return List.of();
    }

    public Collection<ChessMove> basicMoves(ChessBoard board, ChessPosition myPosition, int[][] myDirections, Boolean repeatable) {
        
        // each item in my direction
        
        List<ChessMove> moves = List.of();

        ChessPiece me = board.getPiece(myPosition);
        if (me == null) {return moves;}

        for (int[] d : myDirections) {
            int row = myPosition.getRow() + d[0];
            int col = myPosition.getColumn() + d[1];

            while(isOnBoard(row, col)) {
                //look at tile
                ChessPosition target = new ChessPosition(row, col);
                ChessPiece squatter = board.getPiece(target);

                if (squatter == null) {
                    moves.add(new ChessMove(myPosition, target, null));
                } else {
                    if (squatter.getTeamColor() != me.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    break;
                }
                if (!repeatable) {
                    break;
                }

                row += d[0];
                col += d[1];
            }
        }

        return moves;
    }

    private Boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
