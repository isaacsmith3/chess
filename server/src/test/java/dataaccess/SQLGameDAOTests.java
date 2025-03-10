package dataaccess;

import dataaccess.mysql.MySQLGameDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;
import model.GameData;
import types.JoinGameRequest;
import types.ListGamesResult;

public class SQLGameDAOTests {
    private MySQLGameDAO mySQLGameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mySQLGameDAO = new MySQLGameDAO();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        mySQLGameDAO.clear();
    }

    @Test
    public void positiveTestCreateGame() throws DataAccessException {
        mySQLGameDAO.createGame("testGame");
        Collection<ListGamesResult> games = mySQLGameDAO.getGames();
        Assertions.assertEquals(1, games.size());
        Assertions.assertEquals("testGame", games.iterator().next().gameName());
    }

    @Test
    public void negativeTestCreateGame() throws DataAccessException {
        mySQLGameDAO.createGame("null");
        Assertions.assertThrows(DataAccessException.class, () -> mySQLGameDAO.createGame(null));
    }

    @Test
    public void positiveTestGetGames() throws DataAccessException {
        mySQLGameDAO.createGame("testGame");
        mySQLGameDAO.createGame("testGame2");
        Collection<ListGamesResult> games = mySQLGameDAO.getGames();
        Assertions.assertEquals(2, games.size());
        Assertions.assertEquals("testGame", games.iterator().next().gameName());
    }

    @Test
    public void negativeTestGetGames() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertEquals(new ArrayList<ListGamesResult>(), mySQLGameDAO.getGames());
        mySQLGameDAO.createGame("testGame");
        Assertions.assertEquals(1, mySQLGameDAO.getGames().size());
    }

    @Test
    public void positiveTestJoinGame() throws DataAccessException {
        mySQLGameDAO.createGame("testGame");
        mySQLGameDAO.joinGame(new GameData(1, "testUser", "testUser1", "testGameResult", new ChessGame()));
        Assertions.assertEquals(1, mySQLGameDAO.getGames().size());
        Assertions.assertEquals("testUser", mySQLGameDAO.getGames().iterator().next().whiteUsername());
        Assertions.assertEquals("testUser1", mySQLGameDAO.getGames().iterator().next().blackUsername());
    }

    @Test
    public void negativeTestJoinGame() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertThrows(NullPointerException.class, () -> mySQLGameDAO.joinGame(null));
    }

    @Test
    public void positiveTestGetGame() throws DataAccessException {
        mySQLGameDAO.createGame("testGame");
        mySQLGameDAO.joinGame(new GameData(1, "testUser", "testUser1", "testGameResult", new ChessGame()));
        Assertions.assertEquals(1, mySQLGameDAO.getGame(new JoinGameRequest(1, "BLACK")).gameID());
    }

    @Test
    public void negativeTestGetGame() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertNull(mySQLGameDAO.getGame(new JoinGameRequest(1, "BLACK")));
    }

    @Test
    public void positiveTestGetAllGames() throws DataAccessException {
        mySQLGameDAO.createGame("testGame");
        mySQLGameDAO.createGame("testGame2");
        Assertions.assertEquals(2, mySQLGameDAO.getAllGames().size());
    }

    @Test
    public void negativeTestGetAllGames() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertEquals(0, mySQLGameDAO.getAllGames().size());
    }
}
