package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import types.ListGamesResult;
import ui.EscapeSequences;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.rmi.ServerException;
import java.util.Collection;

public class GameClient {
    private final ServerFacade serverFacade;
    private String playerColor = null;
    private final int gameId;
    private ChessGame chessGame;
//    private String authToken;
    private boolean isObserving;
    private String serverUrl;

    private GameHandler gameHandler;
    private WebSocketFacade webSocketFacade;
    private AuthData authData;

    public GameClient(String serverUrl, String playerColor, String gameId, boolean isObserving, AuthData authData) throws ServerException {
        this.serverUrl = serverUrl;
        this.serverFacade = new ServerFacade(serverUrl);
        this.gameId = Integer.parseInt(gameId);
        this.chessGame = new ChessGame();
        this.isObserving = isObserving;
        this.gameHandler = new GameHandler();
        this.webSocketFacade = new WebSocketFacade(serverUrl, gameHandler);
        this.authData = authData;

        if (!isObserving) {
            this.playerColor = playerColor;
        }

        this.webSocketFacade.connect(this.authData.authToken(), this.gameId, playerColor, this.authData.userName());

    }

//    public void setAuthToken(String authToken) {
//        this.authToken = authToken;
//    }

    public String eval(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "draw":
                return drawBoard(this.playerColor, this.chessGame.getBoard());
            case "leave":
                return "Left game successfully";
        }
        return "Invalid command";
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("draw - redraw the board\n");
        output.append("highlight - highlight the legal moves\n");
        output.append("move - make a move\n");
        output.append("resign - resign and leave the game\n");
        output.append("leave - leave the game\n");
        output.append("quit - quit the program\n");
        return output.toString();
    }

    public String drawBoard(String playerColor, ChessBoard board) {
        boolean isBlack = playerColor.equalsIgnoreCase("BLACK");
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
