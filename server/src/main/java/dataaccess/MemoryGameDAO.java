package dataaccess;

import chess.ChessGame;
import endpoint.GameResult;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {

    private Collection<GameData> gameDataCollection;
    private AtomicInteger gameIdCounter = new AtomicInteger(1);

    public MemoryGameDAO() {
        this.gameDataCollection = new ArrayList<>();
    }

    @Override
    public GameResult createGame(String gameName) {
        int newGameId = gameIdCounter.getAndIncrement();
        GameData game = new GameData(newGameId, null, null, gameName, new ChessGame());
        gameDataCollection.add(game);
        return new GameResult(newGameId);
    }
}
