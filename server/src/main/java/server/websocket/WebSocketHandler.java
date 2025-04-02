package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import types.JoinGameRequest;
import types.UpdateGameRequest;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    // Session

    private final GameService gameService;
    private final ConnectionManager connectionManager = new ConnectionManager();

    public WebSocketHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + reason);
        connectionManager.removeSession(session);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connection established from: " + session.getRemoteAddress());
        System.out.println("Connection parameters: " + session.getUpgradeRequest().getParameterMap());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws GameService.InvalidAuthTokenException, IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println(cmd);

        switch (cmd.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, ResignCommand.class), session);
        }
    }

    private void resign(ResignCommand cmd, Session session) throws GameService.InvalidAuthTokenException, IOException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        try {
            GameData gameData = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            if (gameData == null) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            AuthData authData = gameService.getUserNameByAuthToken(cmd.getAuthToken());
            String username = authData.userName();

            ChessGame chessGame = gameData.game();

            if (chessGame.isGameOver()) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is already over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            boolean isWhitePlayer = username.equals(gameData.whiteUsername());
            boolean isBlackPlayer = username.equals(gameData.blackUsername());

            if (!isWhitePlayer && !isBlackPlayer) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: only players can resign");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            // check if game is already over
            boolean isGameOver = chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.BLACK);

            if (isGameOver) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is already over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            chessGame.setGameOver(true);

            gameService.updateGame(cmd.getAuthToken(), new UpdateGameRequest(cmd.getGameID(), null, chessGame));

            String message = username + " resigned the game";
            Notification notification = new Notification(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    message
            );

            for (Connection connection : connectionManager.connections.values()) {
                if (connection.gameId == cmd.getGameID()) {
                    connectionManager.sendMessage(connection.authToken, notification);
                }
            }

        } catch (GameService.InvalidGameException e) {
            throw new RuntimeException(e);
        } catch (GameService.InvalidCredentialsException e) {
            throw new RuntimeException(e);
        } catch (GameService.InvalidGameRequestException e) {
            throw new RuntimeException(e);
        }

    }

    private void makeMove(MakeMoveCommand cmd, Session session) throws GameService.InvalidAuthTokenException, IOException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        try {
            GameData gameData = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            if (gameData == null) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            AuthData authData = gameService.getUserNameByAuthToken(cmd.getAuthToken());
            String username = authData.userName();

            ChessGame chessGame = gameData.game();
            ChessGame.TeamColor currentTurn = chessGame.getTeamTurn();

            if (chessGame.isGameOver()) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is already over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            boolean isWhitePlayer = username.equals(gameData.whiteUsername());
            boolean isBlackPlayer = username.equals(gameData.blackUsername());

            boolean isGameOver = chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.BLACK);

            if (isGameOver) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            if ((isWhitePlayer && currentTurn != ChessGame.TeamColor.WHITE) ||
                    (isBlackPlayer && currentTurn != ChessGame.TeamColor.BLACK) ||
                    (!isWhitePlayer && !isBlackPlayer)) {

                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: not your turn");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            chessGame.makeMove(cmd.getMove());

            gameService.updateGame(cmd.getAuthToken(), new UpdateGameRequest(cmd.getGameID(), null, chessGame));

            GameData updatedGame = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            LoadGame loadGameMessage = new LoadGame(updatedGame);
            for (Connection connection : connectionManager.connections.values()) {
                if (connection.gameId == cmd.getGameID()) {
                    connectionManager.sendMessage(connection.authToken, loadGameMessage);
                }
            }

            Notification notification = new Notification(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "A move was made from " + cmd.move.getStartPosition() + " to " + cmd.move.getEndPosition()
            );
            connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());

        } catch (GameService.InvalidAuthTokenException e){
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
        } catch (InvalidMoveException e) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid move");
            connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
        } catch (GameService.InvalidGameException e) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid game");
            connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
        } catch (GameService.InvalidCredentialsException e) {
            throw new RuntimeException(e);
        } catch (GameService.InvalidGameRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(ConnectCommand cmd, Session session) throws IOException, GameService.InvalidAuthTokenException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        connectionManager.add(cmd.getAuthToken(), session, cmd.getGameID());

        try {
            GameData game = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            if (game == null) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
            } else {
                LoadGame loadGameMessage = new LoadGame(game);
                connectionManager.sendMessage(cmd.getAuthToken(), loadGameMessage);

                Notification notification = new Notification(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        cmd.username + " connected to the game"
                );
                connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());
            }
        } catch (Exception e) {
            System.err.println("Error in connect handler: " + e.getMessage());
            e.printStackTrace();
        }
    }


}





