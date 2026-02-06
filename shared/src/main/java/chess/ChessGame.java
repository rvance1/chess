package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessBoard board;
    public TeamColor currentTurn;
    
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard(); // Initialize the board to the starting position
        currentTurn = TeamColor.WHITE;
    }
    

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.board);
        hash = 53 * hash + Objects.hashCode(this.currentTurn);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChessGame other = (ChessGame) obj;
        if (!Objects.equals(this.board, other.board)) {
            return false;
        }
        return this.currentTurn == other.currentTurn;
    }

    

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        
        List<ChessMove> validMoves = new ArrayList<>();
        
        ChessPiece me = this.board.getPiece(startPosition);
        if (me == null) {return null;}
        
        for (ChessMove move: me.pieceMoves(this.board, startPosition)) {
            ChessBoard newBoard = simulateMove(move);
            ChessGame newGame = new ChessGame();
            newGame.setBoard(newBoard);
            if (!newGame.isInCheck(me.getTeamColor())) {
                validMoves.add(move);
            }
        
        }
        return validMoves;
    }
    private ChessBoard simulateMove(ChessMove move) {
        ChessBoard newBoard = new ChessBoard();
        // Copy current board pieces into the new board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = this.board.getPiece(pos);
                if (piece != null) {
                    newBoard.addPiece(pos, piece);
                }
            }
        }

        // Perform the move on the copied board
        ChessPiece movingPiece = newBoard.getPiece(move.getStartPosition());
        // clear the start
        newBoard.addPiece(move.getStartPosition(), null);
        if (movingPiece != null) {
            ChessPiece.PieceType promotion = move.getPromotionPiece();
            if (promotion != null) {
                newBoard.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), promotion));
            } else {
                newBoard.addPiece(move.getEndPosition(), movingPiece);
            }
        }

        return newBoard;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || 
        !validMoves.contains(move) || 
        this.board.getPiece(move.getStartPosition()) == null || 
        this.board.getPiece(move.getStartPosition()).getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Invalid move: " + move.toString());
        }

        // Perform the move
        ChessPiece movingPiece = this.board.getPiece(move.getStartPosition());
        // clear the start
        this.board.addPiece(move.getStartPosition(), null);
        if (movingPiece != null) {
            ChessPiece.PieceType promotion = move.getPromotionPiece();
            if (promotion != null) {
                this.board.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), promotion));
            } else {
                this.board.addPiece(move.getEndPosition(), movingPiece);
            }
        }

        // Switch turns
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        return isPositionThreatened(kingPos, teamColor);
    }

    private boolean isPositionThreatened(ChessPosition position, TeamColor color) {

        int direction = color == TeamColor.WHITE ? 1 : -1;

        int[][] directionsBish = new int[][] {
            {1,1},{1,-1},{-1,1},{-1,-1}
        };
        int[][] directionsRook = new int[][] {
            {1,0},{-1,0},{0,1},{0,-1}
        };
        int[][] directionsHorse = new int[][] {
            {2,1},{2,-1},{-2,1},{-2,-1},
            {1,2},{1,-2},{-1,2},{-1,-2},
        };
        int[][] directionsPawn = new int[][] {
            {direction,1}, {direction,-1}
        };

        if (checkMoves(position, ChessPiece.PieceType.BISHOP, directionsBish, true)) {return true;}
        if (checkMoves(position, ChessPiece.PieceType.QUEEN, directionsBish, true)) {return true;}
        if (checkMoves(position, ChessPiece.PieceType.ROOK, directionsRook, true)) {return true;}
        if (checkMoves(position, ChessPiece.PieceType.QUEEN, directionsRook, true)) {return true;}

        if (checkMoves(position, ChessPiece.PieceType.KNIGHT, directionsHorse, false)) {return true;}

        if (checkMoves(position, ChessPiece.PieceType.KING, directionsBish, false)) {return true;}
        if (checkMoves(position, ChessPiece.PieceType.KING, directionsRook, false)) {return true;}

        return checkMoves(position, ChessPiece.PieceType.PAWN, directionsPawn, true);

    }

    private boolean checkMoves(ChessPosition startpos, ChessPiece.PieceType type, int[][] moves, boolean isRepeating) {
        
        ChessPiece me = this.board.getPiece(startpos);

        for (int[] d: moves) {
            
            int row = startpos.getRow();
            int col = startpos.getColumn();
            
            while(isOnBoard(row, col)) {
                row += d[0];
                col += d[1];

                ChessPosition target = new ChessPosition(row, col);
                ChessPiece squatter = this.board.getPiece(target);

                if (squatter != null) {
                    if (squatter.getPieceType() == type && squatter.getTeamColor() != me.getTeamColor()) {
                        return true;
                    }
                    break;
                }

                if (!isRepeating) {
                    break;
                }
            }


        }
        return false;
    }

    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
    
    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If the team is not in check, it cant be checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }

        // can any piece moove to get out of check?
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
