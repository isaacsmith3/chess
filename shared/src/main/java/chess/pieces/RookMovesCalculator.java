package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.PieceMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Begin calculation
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // up
        for (int i = row+1; i <= 8; i++) {
            ChessPosition newPosition = new ChessPosition(i, col);
            if (loop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        // down
        for (int i = row-1; i >= 1; i--) {
            ChessPosition newPosition = new ChessPosition(i, col);
            if (loop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        // left
        for (int i = col-1; i >= 1; i--) {
            ChessPosition newPosition = new ChessPosition(row, i);
            if (loop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        // right
        for (int i = col+1; i <= 8; i++) {
            ChessPosition newPosition = new ChessPosition(row, i);
            if (loop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        return moves;
    }

    private boolean loop(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessMove> moves) {
        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
        if ((!validMove(newMove, myPosition, newPosition, board, moves))) {
            return true;
        }
        moves.add(newMove);
        return false;
    }
}
