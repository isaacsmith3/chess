package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import websocket.commands.*;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;

public class WebSocketFacade extends Endpoint {

    Session session;
    String playerColor = null;
    GameHandler gameHandler;
    private final String serverUrl;

    public WebSocketFacade(String serverUrl, GameHandler gameHandler) throws ServerException {
        this.serverUrl = serverUrl.replace("http", "ws");
        this.gameHandler = gameHandler;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket opened");
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public void connect(String authToken, int gameId, String playerColor, String username) {
        try {
            this.playerColor = playerColor;
            this.gameHandler.setPlayerColor(playerColor);

            URI socketURI = new URI(serverUrl + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleServerMessage(message);
                }
            });

            ConnectCommand cmd = new ConnectCommand(authToken, username, playerColor, gameId);
            sendCommand(cmd);

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.err.println("WebSocket connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleServerMessage(String rawMessage) {
        try {
            JsonObject jsonMsg = new Gson().fromJson(rawMessage, JsonObject.class);

            String typeStr = jsonMsg.get("serverMessageType").getAsString();

            if (typeStr.equals("LOAD_GAME")) {
                LoadGame loadGameMsg = new Gson().fromJson(rawMessage, LoadGame.class);
                gameHandler.loadGame(new Gson().fromJson(rawMessage, LoadGame.class).getGame());
            }
            else if (typeStr.equals("NOTIFICATION")) {
                gameHandler.send(new Gson().fromJson(rawMessage, Notification.class));
            }
            else if (typeStr.equals("ERROR")) {
                String errorMsg = "Unknown error";

                if (jsonMsg.has("errorMessage")) {
                    JsonElement errorElem = jsonMsg.get("errorMessage");

                    if (errorElem.isJsonPrimitive()) {

                        errorMsg = errorElem.getAsJsonPrimitive().toString();
                    }
                }

                gameHandler.error(new websocket.messages.Error(
                        ServerMessage.ServerMessageType.ERROR, errorMsg));
            }
        } catch (Exception e) {
            System.err.println("Error processing the message: " + e.getMessage());

            e.printStackTrace();
        }
    }



    public void leave(String authToken, int gameId) throws IOException {
        LeaveCommand command = new LeaveCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameId
        );
        sendCommand(command);
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        Gson gson = new Gson();
        String commandJson = gson.toJson(command);
        this.session.getBasicRemote().sendText(commandJson);
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws IOException {
        MakeMoveCommand command = new MakeMoveCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameId,
                move
        );
        sendCommand(command);
    }

    public void resign(String authToken, int gameId) throws IOException {
        ResignCommand command = new ResignCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId);
        sendCommand(command);
    }

}
