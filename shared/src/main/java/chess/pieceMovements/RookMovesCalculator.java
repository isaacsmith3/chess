package chess.piece_movements;

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
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if ((!isValidMove(newMove, myPosition, newPosition, board, moves))) {
                break;
            }
            moves.add(newMove);
        }

        // down
        for (int i = row-1; i >= 1; i--) {
            ChessPosition newPosition = new ChessPosition(i, col);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if ((!isValidMove(newMove, myPosition, newPosition, board, moves))) {
                break;
            }
            moves.add(newMove);
        }

        // left
        for (int i = col-1; i >= 1; i--) {
            ChessPosition newPosition = new ChessPosition(row, i);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if ((!isValidMove(newMove, myPosition, newPosition, board, moves))) {
                break;
            }
            moves.add(newMove);
        }

        // right
        for (int i = col+1; i <= 8; i++) {
            ChessPosition newPosition = new ChessPosition(row, i);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if ((!isValidMove(newMove, myPosition, newPosition, board, moves))) {
                break;
            }
            moves.add(newMove);
        }

        return moves;
    }
}
