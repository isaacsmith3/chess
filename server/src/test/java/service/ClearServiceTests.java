package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthTokenDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTests {

    public ClearService clearService;
    public UserDAO userDAO = new MemoryUserDAO();
    public GameDAO gameDAO = new MemoryGameDAO();
    public AuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();

    @BeforeEach
    public void setUp() {
        this.clearService = new ClearService(this.userDAO, this.authTokenDAO, this.gameDAO);
    }

    @Test
    public void emptyDatabase() throws DataAccessException {
        clearService.clear();
        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.isEmpty());
    }

    @Test
    public void fullDatabase() throws DataAccessException {
        clearService.userDAO.createUser(new UserData("isaac", "smith", "1234"));
        clearService.userDAO.createUser(new UserData("shane", "reese", "6789"));
        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 2);

        clearService.gameDAO.createGame("game 1");
        clearService.gameDAO.createGame("game 2");
        clearService.gameDAO.createGame("game 3");
        Collection<GameData> gameDataCollection = gameDAO.getAllGames();
        assertTrue(gameDataCollection.size() == 3);

        clearService.clear();
        userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.isEmpty());
        assertTrue(gameDataCollection.isEmpty());
    }

    @Test
    public void noClear() throws DataAccessException {
        clearService.clear();
        clearService.userDAO.createUser(new UserData("isaac", "smith", "1234"));
        clearService.userDAO.createUser(new UserData("shane", "reese", "6789"));
        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 2);
    }
}
