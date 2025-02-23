package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Begin calculation
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int j = col;

        // right and up
        for (int i = row+1; i <= 8; i++) {
            j++;
            ChessPosition newPosition = new ChessPosition(i, j);
            if (loopBishop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        // left and up
        j = col;
        for (int i = row+1; i <= 8; i++) {
            j--;
            ChessPosition newPosition = new ChessPosition(i, j);
            if (loopBishop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        // right and down
        j = col;
        for (int i = row-1; i >= 1; i--) {
            j++;
            ChessPosition newPosition = new ChessPosition(i, j);
            if (loopBishop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        // left and down
        j = col;
        for (int i = row-1; i >= 1; i--) {
            j--;
            ChessPosition newPosition = new ChessPosition(i, j);
            if (loopBishop(board, myPosition, newPosition, moves)) {
                break;
            }
        }

        return moves;
    }

    private boolean loopBishop(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessMove> moves) {
        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
        if ((!validMove(newMove, myPosition, newPosition, board, moves))) {
            return true;
        }
        moves.add(newMove);
        return false;
    }
}
