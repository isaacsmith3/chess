package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTurn;
    private ChessBoard board;
    private boolean gameOver;

    public ChessGame() {
        currentTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        gameOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }

        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> calculatedMoves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : calculatedMoves) {
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            try {
                board.removePiece(startPosition);
                board.addPiece(move.getEndPosition(), piece);

                if (!isInCheck(piece.getTeamColor())) {
                    validMoves.add(move);
                }
            } finally {
                board.removePiece(move.getEndPosition());
                if (capturedPiece != null) {
                    board.addPiece(move.getEndPosition(), capturedPiece);
                }
                board.addPiece(startPosition, piece);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece piece = board.getPiece(startPosition);
        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException();
        }

        board.removePiece(startPosition);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            ChessPiece newPiece = new ChessPiece(currentTurn, move.getPromotionPiece());
            board.addPiece(endPosition, newPiece);
        }
        else {
            board.addPiece(endPosition, piece);
        }

        if (currentTurn == TeamColor.BLACK) {
            currentTurn = TeamColor.WHITE;
        } else if (currentTurn == TeamColor.WHITE) {
            currentTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> otherTeamMoves = new ArrayList<>();

        // Checking all the moves other team's moves
        for (int i = 1; i <= 8; i++ ) {
            for (int j = 1; j <= 8; j++ ) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    otherTeamMoves.addAll(piece.pieceMoves(board, new ChessPosition(i, j)));
                }
            }
        }

        for (ChessMove move : otherTeamMoves) {
            ChessPosition position = move.getEndPosition();
            ChessPiece kingPiece = board.getPiece(position);
            if (kingPiece != null && kingPiece.getPieceType() == ChessPiece.PieceType.KING && kingPiece.getTeamColor() == teamColor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor) && isInPureStalemate(teamColor)) {
            this.gameOver = true;
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves (but also in check)
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++ ) {
            for (int j = 1; j <= 8; j++ ) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        if (isInCheck(teamColor)) {  // Check if the king is in check
            return false;
        }
        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves (but not in check)
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInPureStalemate(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++ ) {
            for (int j = 1; j <= 8; j++ ) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        // No check if the king is in check
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
