package chess;

import java.util.ArrayList;
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
            return basicMoves(board, myPosition, myDirections, true, false);
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            int [][] myDirection = new int[][] {
                {1,0}, {-1, 0}, {0,1}, {0,-1}
            };
            return basicMoves(board, myPosition, myDirection, true, false);
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            int[][] myDirection = new int [][] {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1},
                {1,0}, {-1, 0}, {0,1}, {0,-1}
            };
            return basicMoves(board, myPosition, myDirection, true, false);
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            int[][] myDirection = new int[][] {
                {2,1}, {2,-1}, {-2,1}, {-2,-1},
                {1,2}, {1,-2}, {-1,2}, {-1,-2}
            };
            return basicMoves(board, myPosition, myDirection, false, false);
        }
        if (piece.getPieceType() == PieceType.KING) {
            int[][] myDirection = new int[][] {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1},
                {1,0}, {-1, 0}, {0,1}, {0,-1}
            };
            return basicMoves(board, myPosition, myDirection, false, false);
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            List<ChessMove> moves = new ArrayList<>();

            int direction = piece.pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
            int starting_row = piece.pieceColor == ChessGame.TeamColor.WHITE ? 2 : 7;

            int[][] myDirections;
            
            //forward moves
            if (myPosition.getRow() == starting_row) {
                myDirections = new int[][] {
                    {direction,0}, {direction,1,1}, {direction,-1,1}
                };
                moves.addAll(basicMoves(board, myPosition, myDirections, true, true));
            } else {
                myDirections = new int[][] {
                    {direction,0}, {direction,1,1}, {direction,-1,1}
                };
                moves.addAll(basicMoves(board, myPosition, myDirections, false, true));
            }
            
            int end_row = (direction + 1)/2 * 7 + 1;

            List<ChessMove> promotionMoves = new ArrayList<>();
            for (ChessMove move : moves) {
                if (move.getEndPosition().getRow() == end_row) {
                    promotionMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.QUEEN));
                    promotionMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.ROOK));
                    promotionMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.BISHOP));
                    promotionMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), PieceType.KNIGHT));
                }
            }
            if (!promotionMoves.isEmpty()) {
                return promotionMoves;
            } else {
                return moves;
            }
        }

        
        return List.of();
    }

    public Collection<ChessMove> basicMoves(ChessBoard board, ChessPosition myPosition, int[][] myDirections, Boolean repeatable, Boolean isPawn) {
        
        // each item in my direction
        
        List<ChessMove> moves = new ArrayList<>();

        ChessPiece me = board.getPiece(myPosition);
        if (me == null) {return moves;}
        
        for (int[] d : myDirections) {
            int row = myPosition.getRow() + d[0];
            int col = myPosition.getColumn() + d[1];

            int i = 0;
            while(isOnBoard(row, col)) {
                //look at tile
                ChessPosition target = new ChessPosition(row, col);
                ChessPiece squatter = board.getPiece(target);

                if (squatter == null) {
                    if (d[0] == 0 && d[1] == 0) {
                        break;
                    }
                    if (d.length == 3) {
                        break;
                    }
                    moves.add(new ChessMove(myPosition, target, null));
                } else {
                    if (squatter.getTeamColor() != me.getTeamColor() && (!isPawn || d.length == 3)) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }

                    break;
                }
                if (!repeatable || d.length == 3) {
                    break;
                }
                if (isPawn && i >= 1) {
                    break;
                }

                row += d[0];
                col += d[1];
                i ++;
            }
        }

        return moves;
    }

    private Boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
