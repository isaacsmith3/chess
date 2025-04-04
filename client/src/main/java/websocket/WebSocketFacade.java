package websocket;

import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
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

    public WebSocketFacade(String url, GameHandler gameHandler) throws ServerException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.gameHandler = gameHandler;

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> gameHandler.send(new Gson().fromJson(message, Notification.class));
                        case ERROR -> gameHandler.error(new Gson().fromJson(message, Error.class));
                        case LOAD_GAME -> loadGame(new Gson().fromJson(message, LoadGame.class));
                    }
                }
            });


        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServerException(ex.getMessage());
        }
    }

    private void loadGame(LoadGame loadGameMessage) {
//        if (loadGameMessage.resign){
//            gameplay.endGame();
//            System.out.println("Press anything to continue");
//            return;
//        }
//        gameplay.updateGame(loadGameMessage.game);
//        gameplay.printBoard(playerColor);
//        gameplay.checkWin();
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameId, String playerColor, String username) {
        this.playerColor = playerColor;
        try {
            ConnectCommand cmd = new ConnectCommand(authToken, username, playerColor, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
//            this.session.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
