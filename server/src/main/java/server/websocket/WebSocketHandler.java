package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import types.JoinGameRequest;
import websocket.commands.ConnectCommand;
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





