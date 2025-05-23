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

    // for all pieces except the pawn
    default boolean validMove(ChessMove possibleMove, ChessPosition startPosition,
                              ChessPosition newPosition, ChessBoard board, Collection<ChessMove> moves) {
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

    default boolean loop(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessMove> moves) {
        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
        if ((!validMove(newMove, myPosition, newPosition, board, moves))) {
            return true;
        }
        moves.add(newMove);
        return false;
    }

}
