package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    public final String authToken;
    public final Session session;
    public final int gameId;
    private static final Gson gson = new Gson();

    public Connection(String authToken, Session session, int gameId) {
        this.authToken = authToken;
        this.session = session;
        this.gameId = gameId;
    }

    public void send(ServerMessage message) throws IOException {
        if (session.isOpen()) {
            String jsonMessage = gson.toJson(message);
            session.getRemote().sendString(jsonMessage);
        }
    }
}
