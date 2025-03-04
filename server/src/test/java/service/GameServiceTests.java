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
import types.JoinGameRequest;
import types.ListGamesResult;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameServiceTests {

    public GameService gameService;
    public UserService userService;
    public GameDAO gameDAO = new MemoryGameDAO();
    public AuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();
    public UserDAO userDAO = new MemoryUserDAO();

    @BeforeEach
    public void setUp() {
        this.gameService = new GameService(gameDAO, authTokenDAO);
        this.userService = new UserService(userDAO, authTokenDAO);
    }

    @Test
    public void positiveTestCreateGame() throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        gameService.createGame(auth, "game1");
        Collection<GameData> gameDataCollection = gameDAO.getAllGames();
        assertTrue(gameDataCollection.size() == 1);

        GameData game = gameDataCollection.iterator().next();
        assertTrue(game.gameName().equals("game1"));

    }

    @Test
    public void negativeTestCreateGame() throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);

        assertThrows(GameService.InvalidAuthTokenException.class, () -> gameService.createGame("1234", "game1"));
    }

    @Test
    public void positiveTestJoinGame()
            throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException, GameService.InvalidGameException,
            GameService.InvalidCredentialsException, GameService.InvalidGameRequestException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        gameService.createGame(auth, "game1");
        Collection<GameData> gameDataCollection = gameDAO.getAllGames();
        assertTrue(gameDataCollection.size() == 1);

        GameData game = gameDataCollection.iterator().next();
        assertTrue(game.gameName().equals("game1"));

        gameService.joinGame(auth, new JoinGameRequest(1, "WHITE"));
        gameDataCollection = gameDAO.getAllGames();
        game = gameDataCollection.iterator().next();
        assertTrue(game.gameName().equals("game1"));
        assertTrue(game.whiteUsername().equals("username"));
    }

    @Test
    public void negativeTestJoinGame() throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        gameService.createGame(auth, "game1");
        Collection<GameData> gameDataCollection = gameDAO.getAllGames();
        assertTrue(gameDataCollection.size() == 1);

        GameData game = gameDataCollection.iterator().next();
        assertTrue(game.gameName().equals("game1"));

        assertThrows(GameService.InvalidAuthTokenException.class, () -> gameService.joinGame("1234", new JoinGameRequest(1, "WHITE")));
    }

    @Test
    public void negativeTestJoinGameDoesNotExist() throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        assertThrows(GameService.InvalidGameRequestException.class, () -> gameService.joinGame(auth, new JoinGameRequest(1, "WHITE")));
    }


    @Test
    public void positiveTestListGames() throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        gameService.createGame(auth, "game1");
        gameService.createGame(auth, "game2");
        gameService.createGame(auth, "game3");
        Collection<GameData> gameDataCollection = gameDAO.getAllGames();
        assertTrue(gameDataCollection.size() == 3);

        Collection<ListGamesResult> gamesList = gameService.listGames(auth);
        assertTrue(gamesList.size() == 3);

    }

    @Test
    public void negativeTestListGames() throws UserService.DuplicateUserException, GameService.InvalidAuthTokenException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        gameService.createGame(auth, "game1");
        gameService.createGame(auth, "game2");
        gameService.createGame(auth, "game3");
        Collection<GameData> gameDataCollection = gameDAO.getAllGames();
        assertTrue(gameDataCollection.size() == 3);

       assertThrows(GameService.InvalidAuthTokenException.class, () -> gameService.listGames("1234"));

    }

}
