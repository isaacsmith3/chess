package dataaccess;

import endpoint.CreateGameResult;
import endpoint.JoinGameRequest;
import endpoint.ListGamesResult;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    CreateGameResult createGame(String gameName);
    GameData getGame(JoinGameRequest gameRequest);
    void joinGame(GameData updatedGame);
    Collection<ListGamesResult> getGames();
    void clear();
}
