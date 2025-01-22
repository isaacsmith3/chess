package chess;

import chess.piece_movements.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;
    private final ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.teamColor = pieceColor;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", teamColor=" + teamColor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();

        if (piece != null) {
            switch (piece.type) {
                case BISHOP:
                    PieceMovesCalculator movesCalculator = new BishopMovesCalculator();
                    moves.addAll(movesCalculator.pieceMoves(board, myPosition));
                    break;
                case ROOK:
                    PieceMovesCalculator movesCalculator2 = new RookMovesCalculator();
                    moves.addAll(movesCalculator2.pieceMoves(board, myPosition));
                    break;
                case QUEEN:
                    PieceMovesCalculator movesCalculator3 = new QueenMovesCalculator();
                    moves.addAll(movesCalculator3.pieceMoves(board, myPosition));
                    break;
                case KNIGHT:
                    PieceMovesCalculator movesCalculator4 = new KnightMovesCalculator();
                    moves.addAll(movesCalculator4.pieceMoves(board, myPosition));
                    break;
                case KING:
                    PieceMovesCalculator movesCalculator5 = new KingMovesCalculator();
                    moves.addAll(movesCalculator5.pieceMoves(board, myPosition));
                    break;
                case PAWN:
                    PieceMovesCalculator movesCalculator6 = new PawnMovesCalculator();
                    moves.addAll(movesCalculator6.pieceMoves(board, myPosition));
                    break;
            }
        }

        return moves;
    }
}
