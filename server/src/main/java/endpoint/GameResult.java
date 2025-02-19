package endpoint;

public class GameResult {
    private Integer gameID;

    public GameResult() {}

    public GameResult(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(int gameId) {
        this.gameID = gameId;
    }
}
