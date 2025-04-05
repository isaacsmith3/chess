package websocket;

import chess.*;
import model.GameData;
import ui.EscapeSequences;
import websocket.messages.Notification;

import javax.print.DocFlavor;
import java.util.Collection;

public class GameHandler {

    public boolean gameOver;
    public ChessGame chessGame;
    private String playerColor;

    public void loadGame(GameData game) {
        this.chessGame = game.game();
        drawBoard(playerColor, chessGame.getBoard());
        String boardStr = drawBoard(playerColor, chessGame.getBoard());
        System.out.println(boardStr);

    }


    public void send(Notification notification) {
        System.out.println("NOTIFICATION: " + notification.getMessage());
    }

    public void error(websocket.messages.Error errorMessage) {
        System.err.println("ERROR: " + errorMessage.getErrorMessage());
    }

    public void setPlayerColor(String color) {
        this.playerColor = color;
    }

    public String drawBoard(String playerColor, ChessBoard board) {
        boolean isBlack = playerColor != null && playerColor.equalsIgnoreCase("BLACK");
        StringBuilder boardOutput = new StringBuilder();
        boardOutput.append("\n");
        boardOutput.append("\n");

        boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);

        printLetters(isBlack, boardOutput);
        boardOutput.append("  ");

        boardOutput.append(EscapeSequences.RESET_BG_COLOR);
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardOutput.append("\n");

        for (int row = 0; row < 8; row++) {
            int displayRow = isBlack ? row + 1 : 8 - row;

            // Set light grey background for row number
            boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(displayRow).append(" ");

            for (int col = 0; col < 8; col++) {
                int displayCol = isBlack ? 8 - col : col + 1;

                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                boardOutput.append(bgColor);

                ChessPosition position = new ChessPosition(displayRow, displayCol);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    String textColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE
                            ? EscapeSequences.SET_TEXT_COLOR_BLUE
                            : EscapeSequences.SET_TEXT_COLOR_RED;

                    String pieceChar = getPieceChar(piece.getPieceType());
                    boardOutput.append(" ").append(textColor).append(pieceChar).append(" ");
                } else {
                    boardOutput.append("   ");
                }
            }

            // Set light grey only for the row number at the end
            boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(" ").append(displayRow);

            // Reset background color before newline
            boardOutput.append(EscapeSequences.RESET_BG_COLOR);
            boardOutput.append("\n");
        }

        // Set light grey for the bottom letters
        boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        printLetters(isBlack, boardOutput);

        boardOutput.append(EscapeSequences.RESET_BG_COLOR);
        boardOutput.append(EscapeSequences.RESET_TEXT_COLOR);
        boardOutput.append("  ");
        boardOutput.append("\n");

        return boardOutput.toString();
    }


    private void printLetters(boolean isBlack, StringBuilder boardOutput) {
        boardOutput.append("  ");
        for (int col = 0; col < 8; col++) {
            char file;
            if (isBlack) {
                file = (char) ('a' + (7 - col));
            } else {
                file = (char) ('a' + col);
            }
            boardOutput.append(" ").append(file).append(" ");
        }
    }

    private String getPieceChar(ChessPiece.PieceType type) {
        switch (type) {
            case KING: return "K";
            case QUEEN: return "Q";
            case BISHOP: return "B";
            case KNIGHT: return "N";
            case ROOK: return "R";
            case PAWN: return "P";
            default: return " ";
        }
    }

    public void highlight(ChessPiece piece, ChessPosition position, Boolean isBlack) {
        if (chessGame == null || piece == null) {
            System.out.println("Error: no piece or chess game");
            return;
        }
        Collection<ChessMove> validMoves = chessGame.validMoves(position);
        if (validMoves.isEmpty()) {
            System.out.println("No valid moves for this piece. Choose a different piece");
            return;
        }
        StringBuilder boardOutput = new StringBuilder();
        boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardOutput.append("\n");
        printLetters(isBlack, boardOutput);
        boardOutput.append("\n");
        ChessBoard board = chessGame.getBoard();
        for (int row = 0; row < 8; row++) {
            int displayRow = isBlack ? row + 1 : 8 - row;
            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(displayRow).append(" ");
            for (int col = 0; col < 8; col++) {
                int displayCol = isBlack ? 8 - col : col + 1;
                ChessPosition currentPosition = new ChessPosition(displayRow, displayCol);
                boolean isValidMove = false;
                for (ChessMove move : validMoves) {
                    ChessPosition endPos = move.getEndPosition();
                    if (endPos.getRow() == currentPosition.getRow() &&
                            endPos.getColumn() == currentPosition.getColumn()) {
                        isValidMove = true;
                        break;
                    }
                }
                boolean isSelectedPiece = (currentPosition.getRow() == position.getRow() &&
                        currentPosition.getColumn() == position.getColumn());
                boolean isLightSquare = (row + col) % 2 == 0;
                String backgroundColor;
                if (isValidMove) {
                    backgroundColor = EscapeSequences.SET_BG_COLOR_GREEN;
                } else if (isSelectedPiece) {
                    backgroundColor = EscapeSequences.SET_BG_COLOR_YELLOW;
                } else {
                    backgroundColor = isLightSquare ?
                            EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                }
                boardOutput.append(backgroundColor);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null) {
                    String textColor = currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE
                            ? EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
                    String pieceChar = getPieceChar(currentPiece.getPieceType());
                    boardOutput.append(" ").append(textColor).append(pieceChar).append(" ");
                } else {
                    boardOutput.append("   ");
                }
            }
            boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(" ").append(displayRow).append("\n");
        }
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        printLetters(isBlack, boardOutput);
        boardOutput.append(EscapeSequences.RESET_BG_COLOR);
        boardOutput.append(EscapeSequences.RESET_TEXT_COLOR);
        boardOutput.append("\n");
        System.out.println(boardOutput);
    }
}

















