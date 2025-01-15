package chess.piece_movements;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.PieceMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Begin calculation
        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        // right side
        ChessPosition position1 = new ChessPosition(row+2, col+1);
        ChessMove move1 = new ChessMove(myPosition, position1, null);
        if ((isValidMove(move1, myPosition, position1, board, moves))) {
            moves.add(move1);
        }

        ChessPosition position2 = new ChessPosition(row+1, col+2);
        ChessMove move2 = new ChessMove(myPosition, position2, null);
        if ((isValidMove(move2, myPosition, position2, board, moves))) {
            moves.add(move2);
        }

        ChessPosition position3 = new ChessPosition(row-1, col+2);
        ChessMove move3 = new ChessMove(myPosition, position3, null);
        if ((isValidMove(move3, myPosition, position3, board, moves))) {
            moves.add(move3);
        }

        ChessPosition position4 = new ChessPosition(row-2, col+1);
        ChessMove move4 = new ChessMove(myPosition, position4, null);
        if ((isValidMove(move4, myPosition, position4, board, moves))) {
            moves.add(move4);
        }

        // left side
        ChessPosition position5 = new ChessPosition(row-2, col-1);
        ChessMove move5 = new ChessMove(myPosition, position5, null);
        if ((isValidMove(move5, myPosition, position5, board, moves))) {
            moves.add(move5);
        }

        ChessPosition position6 = new ChessPosition(row-1, col-2);
        ChessMove move6 = new ChessMove(myPosition, position6, null);
        if ((isValidMove(move6, myPosition, position6, board, moves))) {
            moves.add(move6);
        }

        ChessPosition position7 = new ChessPosition(row+2, col-1);
        ChessMove move7 = new ChessMove(myPosition, position7, null);
        if ((isValidMove(move7, myPosition, position7, board, moves))) {
            moves.add(move7);
        }

        ChessPosition position8 = new ChessPosition(row+1, col-2);
        ChessMove move8 = new ChessMove(myPosition, position8, null);
        if ((isValidMove(move8, myPosition, position8, board, moves))) {
            moves.add(move8);
        }

        return moves;
    }
}
