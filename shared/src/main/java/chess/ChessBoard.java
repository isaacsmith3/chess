package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    public void removePiece(ChessPosition position) {
        this.board[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPosition position;
        ChessPiece piece;

        for (int i = 1; i <= 8; i++) {
            position = new ChessPosition(2, i); // white pawn
            piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            this.addPiece(position, piece);

            position = new ChessPosition(7, i); // black pawn
            piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            this.addPiece(position, piece);
        }

        // ROOKS
        position = new ChessPosition(1, 1);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        this.addPiece(position, piece);

        position = new ChessPosition(1, 8);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 1);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 8);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        this.addPiece(position, piece);

        // KNIGHTS
        position = new ChessPosition(1, 2);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        this.addPiece(position, piece);

        position = new ChessPosition(1, 7);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 2);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 7);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        this.addPiece(position, piece);

        // BISHOPS
        position = new ChessPosition(1, 3);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        this.addPiece(position, piece);

        position = new ChessPosition(1, 6);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 3);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 6);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        this.addPiece(position, piece);

        // QUEENS
        position = new ChessPosition(1, 4);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 4);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        this.addPiece(position, piece);

        // KINGS
        position = new ChessPosition(1, 5);
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        this.addPiece(position, piece);

        position = new ChessPosition(8, 5);
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        this.addPiece(position, piece);

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }
}
