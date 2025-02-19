package dataaccess;

import endpoint.GameResult;
import endpoint.JoinGameRequest;
import model.GameData;

public interface GameDAO {
    GameResult createGame(String gameName);
    GameData getGame(JoinGameRequest gameRequest);
    void joinGame(GameData updatedGame);
}
