package dataaccess;

import endpoint.GameResult;

public interface GameDAO {
    GameResult createGame(String gameName);

}
