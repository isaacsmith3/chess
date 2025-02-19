package dataaccess;

import chess.ChessGame;
import types.CreateGameResult;
import types.JoinGameRequest;
import types.ListGamesResult;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemoryGameDAO implements GameDAO {

    private Collection<GameData> gameDataCollection;
    private AtomicInteger gameIdCounter = new AtomicInteger(1);

    public MemoryGameDAO() {
        this.gameDataCollection = new ArrayList<>();
    }

    @Override
    public CreateGameResult createGame(String gameName) {
        int newGameId = gameIdCounter.getAndIncrement();
        GameData game = new GameData(newGameId, null, null, gameName, new ChessGame());
        gameDataCollection.add(game);
        return new CreateGameResult(newGameId);
    }

    @Override
    public GameData getGame(JoinGameRequest gameRequest) {
        for (GameData game : gameDataCollection) {
            if (game.gameId() == gameRequest.gameID()) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void joinGame(GameData updatedGame) {
        for (GameData game : gameDataCollection) {
            if (game.gameId() == updatedGame.gameId()) {
                gameDataCollection.remove(game);
                gameDataCollection.add(updatedGame);
                return;
            }
        }
    }

    @Override
    public Collection<ListGamesResult> getGames() {
        return gameDataCollection.stream().map(game -> new ListGamesResult(game.gameId(), game.whiteUsername(), game.blackUsername(), game.gameName())).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        gameDataCollection.clear();
    }


}
