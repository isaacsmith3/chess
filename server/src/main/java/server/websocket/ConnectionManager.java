package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session, int gameId) {
        var connection = new Connection(visitorName, session, gameId);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeAuthToken, ServerMessage msg, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuthToken)) {
                    if(c.gameId == gameID){
                        c.send(msg);
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void sendMessage(String authToken, ServerMessage message) throws IOException {
        var c = connections.get(authToken);
        c.send(message);
    }

    public void removeSession(Session session) {
        connections.entrySet().removeIf(entry -> entry.getValue().session.equals(session));
    }


}
