package dataaccess;

import types.CreateGameResult;
import types.JoinGameRequest;
import types.ListGamesResult;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    CreateGameResult createGame(String gameName);
    GameData getGame(JoinGameRequest gameRequest);
    void joinGame(GameData updatedGame);
    Collection<ListGamesResult> getGames();
    void clear();
    Collection<GameData> getAllGames();
}
