package chess.pieceMovements;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.PieceMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        PieceMovesCalculator movesCalculator = new BishopMovesCalculator();
        moves.addAll(movesCalculator.pieceMoves(board, myPosition));

        PieceMovesCalculator movesCalculator2 = new RookMovesCalculator();
        moves.addAll(movesCalculator2.pieceMoves(board, myPosition));

        return moves;
    }
}
