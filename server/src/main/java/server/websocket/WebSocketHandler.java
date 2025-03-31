package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final GameService gameService;
    private final ConnectionManager connectionManager = new ConnectionManager();

    public WebSocketHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameService = new GameService(gameDAO, authTokenDAO);
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
    public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
        System.out.println("in web socket handler");
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
    }


}
