package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean outsideBoard(ChessPosition myPosition) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        if (col > 8 || col < 1 || row > 8 || row < 1) {
            return true;
        }

        return false;
    }

    default boolean isValidMove(ChessMove possibleMove, ChessPosition startPosition, ChessPosition newPosition, ChessBoard board, Collection<ChessMove> moves) {
        if (outsideBoard(newPosition)) { // off the board
            return false;
        }

        ChessPiece piece = board.getPiece(newPosition);
        if (piece != null) {
            if (piece.getTeamColor() == board.getPiece(startPosition).getTeamColor()) { // same team
                return false;
            } else { // capture
                moves.add(possibleMove);
                return false;
            }
        }

        return true;
    }

}
