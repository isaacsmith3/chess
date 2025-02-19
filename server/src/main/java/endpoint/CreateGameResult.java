package endpoint;

public class CreateGameResult {
    private Integer gameID;

    public CreateGameResult() {}

    public CreateGameResult(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(int gameId) {
        this.gameID = gameId;
    }
}
