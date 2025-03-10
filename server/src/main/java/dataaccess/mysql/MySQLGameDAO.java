package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import types.CreateGameResult;
import types.JoinGameRequest;
import types.ListGamesResult;

import java.sql.*;
import java.util.ArrayList;
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
        int newGameID = gameIdCounter.getAndIncrement();
        GameData game = new GameData(newGameID, null, null, gameName, new ChessGame());
        var jsonChess = new Gson().toJson(game);
        var SQLStatement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, jsonChessGame) VALUES (?, ?, ?, ?, ?) ";
        databaseManager.executeUpdate(SQLStatement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), jsonChess);
        return new CreateGameResult(newGameID);
    }

    @Override
    public GameData getGame(JoinGameRequest gameRequest) {
        try (Connection conn = databaseManager.getConnection()) {
            String SQLStatement = "SELECT gameId, whiteUsername, blackUsername, gameName, jsonChessGame FROM games WHERE gameId = ?";
            try (PreparedStatement ps = conn.prepareStatement(SQLStatement)) {
                ps.setString(1, String.valueOf(gameRequest.gameID()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("jsonChessGame"), ChessGame.class);
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        );
                    }
                    return null;
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinGame(GameData updatedGame) {
        try (Connection conn = databaseManager.getConnection()) {
            String SQLStatement = "UPDATE games SET whiteUserName = ?, blackUserName = ?, jsonChessGame = ? WHERE gameID = ?";

            try (PreparedStatement ps = conn.prepareStatement(SQLStatement)) {
                if (updatedGame.whiteUsername() == null) {
                    ps.setNull(1, Types.VARCHAR);
                } else {
                    ps.setString(1, updatedGame.whiteUsername());
                }
                if (updatedGame.blackUsername() == null) {
                    ps.setNull(2, Types.VARCHAR);
                } else {
                    ps.setString(2, updatedGame.blackUsername());
                }
                ps.setString(3, new Gson().toJson(updatedGame));
                ps.setInt(4, updatedGame.gameID());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Collection<ListGamesResult> getGames() {
        Collection<ListGamesResult> games = new ArrayList<>();
        try (Connection conn = databaseManager.getConnection()) {
            String SQLStatement = "SELECT gameID, gameName, jsonChessGame FROM games";
            try (PreparedStatement ps = conn.prepareStatement(SQLStatement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ListGamesResult game = new Gson().fromJson(rs.getString("jsonChessGame"), ListGamesResult.class);
                        games.add(game);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return games;
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
