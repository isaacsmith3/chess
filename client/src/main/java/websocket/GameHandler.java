package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;
import websocket.messages.Notification;

public class GameHandler {

    public ChessGame chessGame;
    private String playerColor;

    public void loadGame(GameData game) {
        this.chessGame = game.game();
//        System.out.println("Game updated");
        drawBoard(playerColor, chessGame.getBoard());
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

    public ChessGame getChessGame() {
        return chessGame;
    }

    public String drawBoard(String playerColor, ChessBoard board) {
        boolean isBlack = playerColor != null && playerColor.equalsIgnoreCase("BLACK");
        StringBuilder boardOutput = new StringBuilder();

        boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);

        boardOutput.append("\n");
        printLetters(isBlack, boardOutput);

        boardOutput.append("\n");

        for (int row = 0; row < 8; row++) {
            int displayRow = isBlack ? row + 1 : 8 - row;

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

            boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);

            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(" ").append(displayRow).append("\n");
        }

        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);

        printLetters(isBlack, boardOutput);

        boardOutput.append(EscapeSequences.RESET_BG_COLOR);
        boardOutput.append(EscapeSequences.RESET_TEXT_COLOR);
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
}
