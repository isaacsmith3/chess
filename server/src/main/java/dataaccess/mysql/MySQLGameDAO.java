package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import types.CreateGameResult;
import types.JoinGameRequest;
import types.ListGamesResult;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLGameDAO implements GameDAO {
    public final DatabaseManager databaseManager = new DatabaseManager();
    private AtomicInteger gameIdCounter = new AtomicInteger(1);

    public MySQLGameDAO() throws DataAccessException {
        databaseManager.configureDatabase();
    }

    @Override
    public CreateGameResult createGame(String gameName) throws DataAccessException {
        int newGameId = gameIdCounter.getAndIncrement();
        GameData game = new GameData(newGameId, null, null, gameName, new ChessGame());
        var jsonChess = new Gson().toJson(game);
        var SQLStatement = "INSERT INTO games (gameId, whiteUsername, blackUsername, gameName, jsonChessGame) VALUES (?, ?, ?, ?, ?) ";
        databaseManager.executeUpdate(SQLStatement, game.gameId(), game.whiteUsername(), game.blackUsername(), game.gameName(), jsonChess);
        return new CreateGameResult(newGameId);
    }

    @Override
    public GameData getGame(JoinGameRequest gameRequest) {
        return null;
    }

    @Override
    public void joinGame(GameData updatedGame) {

    }

    @Override
    public Collection<ListGamesResult> getGames() {
        return List.of();
    }

    @Override
    public void clear() {
        try {
            var SQLStatement = "DELETE FROM games";
            databaseManager.executeUpdate(SQLStatement);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GameData> getAllGames() {
        return List.of();
    }
}
