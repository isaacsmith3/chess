package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;
    public int gameId;

    public Connection(String authToken, Session session, int gameId) {
        this.authToken = authToken;
        this.session = session;
        this.gameId = gameId;
    }

    public void send(ServerMessage msg) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(msg);
        session.getRemote().sendString(json);
    }
}