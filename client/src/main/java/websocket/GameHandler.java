package websocket;

import chess.ChessGame;
import websocket.messages.Notification;

public class GameHandler {

    public ChessGame chessGame;

    public void send(Notification notificationMessage) {
        System.out.println(notificationMessage.getMessage());
    }

    public void error(Error errorMessage) {
        System.out.println(errorMessage.getMessage());
    }
}
