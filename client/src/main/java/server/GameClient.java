package server;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import types.ListGamesResult;
import ui.EscapeSequences;

import java.util.Collection;

public class GameClient {
    private final ServerFacade serverFacade;
    private final String playerColor;
    private final int gameId;
    private ChessGame chessGame;
    private String authToken;

    public GameClient(String serverUrl, String playerColor, String gameId) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.playerColor = playerColor;
        this.gameId = Integer.parseInt(gameId);
        this.chessGame = new ChessGame();
    }


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public String eval(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "draw":
                return drawBoard(this.playerColor, this.chessGame.getBoard());
        }
        return "Invalid command";
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("draw - redraw the board\n");
        output.append("quit - quit the program\n");

        return output.toString();
    }

    public String drawBoard(String playerColor, ChessBoard board) {

        return "BOARD";
    }


}
