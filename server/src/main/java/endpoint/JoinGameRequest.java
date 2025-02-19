package endpoint;

public class JoinGameRequest {
    private int gameID;
    private String playerColor;

    public JoinGameRequest(int gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameId() {
        return gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public void setGameId(int gameId) {
        this.gameID = gameId;
    }

}
