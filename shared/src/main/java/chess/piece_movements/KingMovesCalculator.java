package chess.piece_movements;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.PieceMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Begin calculation
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int j = col;

        // up
        ChessPosition newPosition = new ChessPosition(row+1, col);
        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // down
        newPosition = new ChessPosition(row-1, col);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // left
        newPosition = new ChessPosition(row, col-1);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // right
        newPosition = new ChessPosition(row, col+1);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // right and up
        newPosition = new ChessPosition(row+1, col+1);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // right and down
        newPosition = new ChessPosition(row-1, col+1);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // left and up
        newPosition = new ChessPosition(row+1, col-1);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        // left and down
        newPosition = new ChessPosition(row-1, col-1);
        newMove = new ChessMove(myPosition, newPosition, null);
        if ((isValidMove(newMove, myPosition, newPosition, board, moves))) {
            moves.add(newMove);
        }

        return moves;
    }
}
