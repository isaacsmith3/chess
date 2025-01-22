package chess.piece_movements;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Begin calculation
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
            if (row + 1 == 8) {
                promotion = true;
            } else {
                promotion = false;
            }

            // up 1
            ChessPosition newPosition = new ChessPosition(row+1, col);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null) {
                if (promotion) {
                    ChessMove bishopPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP);
                    moves.add(bishopPromotionMove);
                    ChessMove rookPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK);
                    moves.add(rookPromotionMove);
                    ChessMove knightPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT);
                    moves.add(knightPromotionMove);
                    ChessMove queenPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN);
                    moves.add(queenPromotionMove);
                } else {
                    moves.add(newMove);
                }
            }

            ChessPosition attackLeft = new ChessPosition(row+1, col-1);
            ChessMove leftMove = new ChessMove(myPosition, attackLeft, null);
            if (!outsideBoard(attackLeft) && board.getPiece(attackLeft) != null && board.getPiece(attackLeft).getTeamColor() == ChessGame.TeamColor.BLACK) {
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

            ChessPosition attackRight = new ChessPosition(row+1, col+1);
            ChessMove rightMove = new ChessMove(myPosition, attackRight, null);
            if (!outsideBoard(attackRight) && board.getPiece(attackRight) != null && board.getPiece(attackRight).getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (promotion) {
                    ChessMove bishopPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.BISHOP);
                    moves.add(bishopPromotionMove);
                    ChessMove rookPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.ROOK);
                    moves.add(rookPromotionMove);
                    ChessMove knightPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.KNIGHT);
                    moves.add(knightPromotionMove);
                    ChessMove queenPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.QUEEN);
                    moves.add(queenPromotionMove);
                } else {
                    moves.add(rightMove);
                }
            }


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
            if (row - 1 == 1) {
                promotion = true;
            } else {
                promotion = false;
            }

            // down 1
            ChessPosition newPosition = new ChessPosition(row-1, col);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null) {
                if (promotion) {
                    ChessMove bishopPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP);
                    moves.add(bishopPromotionMove);
                    ChessMove rookPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK);
                    moves.add(rookPromotionMove);
                    ChessMove knightPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT);
                    moves.add(knightPromotionMove);
                    ChessMove queenPromotionMove = new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN);
                    moves.add(queenPromotionMove);
                } else {
                    moves.add(newMove);
                }
            }

            ChessPosition attackLeft = new ChessPosition(row-1, col-1);
            ChessMove leftMove = new ChessMove(myPosition, attackLeft, null);
            if (!outsideBoard(attackLeft) && board.getPiece(attackLeft) != null && board.getPiece(attackLeft).getTeamColor() == ChessGame.TeamColor.WHITE) {
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

            ChessPosition attackRight = new ChessPosition(row-1, col+1);
            ChessMove rightMove = new ChessMove(myPosition, attackRight, null);
            if (!outsideBoard(attackRight) && board.getPiece(attackRight) != null && board.getPiece(attackRight).getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (promotion) {
                    ChessMove bishopPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.BISHOP);
                    moves.add(bishopPromotionMove);
                    ChessMove rookPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.ROOK);
                    moves.add(rookPromotionMove);
                    ChessMove knightPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.KNIGHT);
                    moves.add(knightPromotionMove);
                    ChessMove queenPromotionMove = new ChessMove(myPosition, attackRight, ChessPiece.PieceType.QUEEN);
                    moves.add(queenPromotionMove);
                } else {
                    moves.add(rightMove);
                }
            }


        }

        return moves;
    }
}
