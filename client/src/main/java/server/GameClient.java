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

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Collection;

public class GameClient {
    private final ServerFacade serverFacade;
    private String playerColor = null;
    private final int gameId;
    private ChessGame chessGame;
    private boolean isObserving;
    private String serverUrl;
    private GameHandler gameHandler;
    private WebSocketFacade webSocketFacade;
    private AuthData authData;

    public GameClient(String serverUrl, String playerColor, int gameId, boolean isObserving, AuthData authData) throws ServerException {
        this.serverUrl = serverUrl;
        this.serverFacade = new ServerFacade(serverUrl);
        this.gameId = gameId;
        this.chessGame = new ChessGame();
        this.isObserving = isObserving;
        this.gameHandler = new GameHandler();
        this.gameHandler.setPlayerColor(playerColor);
        this.webSocketFacade = new WebSocketFacade(serverUrl, gameHandler);
        this.authData = authData;

        if (!isObserving) {
            this.playerColor = playerColor;
        }

        this.webSocketFacade.connect(this.authData.authToken(), this.gameId, playerColor, this.authData.userName());

    }

    public String eval(String input) throws IOException {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "draw":
                return gameHandler.drawBoard(this.playerColor, this.chessGame.getBoard());
            case "leave":
                webSocketFacade.leave(authData.authToken(), gameId);
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

}
