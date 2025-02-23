package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece piece = board.getPiece(myPosition);

        /* Pawn Cases

        1. Starting row + 1
        2. If starting row + 1, check starting row + 2
        3. Check attacks on both sides
        4. Nothing in front of the pawn, no moving forward
        5. Promotion

         */

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) { // white pawn
            if (row == 2) { // start position
                // up 2
                ChessPosition betweenPosition = new ChessPosition(row+1, col);
                ChessPosition newPosition = new ChessPosition(row+2, col);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                if (board.getPiece(newPosition) == null && board.getPiece(betweenPosition) == null) {
                    moves.add(newMove);
                }

            }
            // check for promotion
            boolean promotion;
            promotion = row + 1 == 8;

            // up 1
            ChessPosition newPosition = new ChessPosition(row+1, col);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            newPosNull(board, myPosition, moves, promotion, newPosition, newMove);

            ChessPosition attackLeft = new ChessPosition(row+1, col-1);
            attackLeft(board, myPosition, moves, promotion, attackLeft);

            ChessPosition attackRight = new ChessPosition(row+1, col+1);
            attackLeft(board, myPosition, moves, promotion, attackRight);
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) { // black pawn
            if (row == 7) { // start position
                // down 2
                ChessPosition betweenPosition = new ChessPosition(row-1, col);
                ChessPosition newPosition = new ChessPosition(row-2, col);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                if (board.getPiece(newPosition) == null && board.getPiece(betweenPosition) == null) {
                    moves.add(newMove);
                }

            }
            // check for promotion
            boolean promotion;
            promotion = row - 1 == 1;

            // down 1
            ChessPosition newPosition = new ChessPosition(row-1, col);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            newPosNull(board, myPosition, moves, promotion, newPosition, newMove);

            ChessPosition attackLeft = new ChessPosition(row-1, col-1);
            leftAttack(board, myPosition, moves, promotion, attackLeft);

            ChessPosition attackRight = new ChessPosition(row-1, col+1);
            leftAttack(board, myPosition, moves, promotion, attackRight);
        }

        return moves;
    }

    private void leftAttack(ChessBoard board,ChessPosition myPosition,Collection<ChessMove> moves,boolean promotion,ChessPosition attackLeft) {
        ChessMove leftMove = new ChessMove(myPosition, attackLeft, null);
        if (!outsideBoard(attackLeft) && board.getPiece(attackLeft) != null && board.getPiece(attackLeft).getTeamColor() == ChessGame.TeamColor.WHITE) {
            left(myPosition, moves, promotion, attackLeft, leftMove);
        }
    }

    private void left(ChessPosition myPosition, Collection<ChessMove> moves, boolean promotion, ChessPosition attackLeft, ChessMove leftMove) {
        if (promotion) {
            ChessMove bishopPromotionMove = new ChessMove(myPosition, attackLeft, ChessPiece.PieceType.BISHOP);
            moves.add(bishopPromotionMove);
            ChessMove rookPromotionMove = new ChessMove(myPosition, attackLeft, ChessPiece.PieceType.ROOK);
            moves.add(rookPromotionMove);
            ChessMove knightPromotionMove = new ChessMove(myPosition, attackLeft, ChessPiece.PieceType.KNIGHT);
            moves.add(knightPromotionMove);
            ChessMove queenPromotionMove = new ChessMove(myPosition, attackLeft, ChessPiece.PieceType.QUEEN);
            moves.add(queenPromotionMove);
        } else {
            moves.add(leftMove);
        }
    }

    private void attackLeft(ChessBoard board,ChessPosition myPosition,Collection<ChessMove> moves,boolean promotion,ChessPosition attackLeft) {
        ChessMove leftMove = new ChessMove(myPosition, attackLeft, null);
        if (!outsideBoard(attackLeft) && board.getPiece(attackLeft) != null && board.getPiece(attackLeft).getTeamColor() == ChessGame.TeamColor.BLACK) {
            left(myPosition, moves, promotion, attackLeft, leftMove);
        }
    }

    private void newPosNull(ChessBoard board,ChessPosition myPosition,Collection<ChessMove> moves,boolean promotion,ChessPosition newPosition,ChessMove newMove) {
        if (board.getPiece(newPosition) == null) {
            left(myPosition, moves,
                    promotion, newPosition, newMove);
        }
    }
}