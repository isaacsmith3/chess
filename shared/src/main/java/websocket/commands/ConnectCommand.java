package websocket.commands;

public class ConnectCommand extends UserGameCommand {

    public String username;
    public String playerColor;

    public ConnectCommand(String authToken, String username, String playerColor, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);
        this.username = username;
        this.playerColor = playerColor;
    }
}
