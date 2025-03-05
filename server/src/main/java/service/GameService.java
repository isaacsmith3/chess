package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import types.CreateGameResult;
import types.JoinGameRequest;
import types.ListGamesResult;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {
    private GameDAO gameDAO;
    private AuthTokenDAO authTokenDAO;

    public GameService(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameDAO = gameDAO;
        this.authTokenDAO = authTokenDAO;
    }

    public CreateGameResult createGame(String authToken, String gameName) throws InvalidAuthTokenException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);

            if (verifiedAuth == null) {
                throw new InvalidAuthTokenException("Invalid auth token");
            }

            return gameDAO.createGame(gameName);
        } catch (DataAccessException e) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
    }

    public void joinGame(String authToken, JoinGameRequest gameRequest) throws InvalidAuthTokenException, InvalidGameException, InvalidCredentialsException, InvalidGameRequestException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);
            String playerColor = gameRequest.playerColor();

            if (verifiedAuth == null) {
                throw new InvalidAuthTokenException("Invalid auth token");
            }

            GameData game = gameDAO.getGame(gameRequest);

            if (game == null) {
                throw new InvalidGameRequestException("Game does not exist");
            }

            GameData updatedGame;

            if (Objects.equals(playerColor, "WHITE")) {
                if (game.whiteUsername() == null) {
                    updatedGame = new GameData(game.gameId(), verifiedAuth.userName(), game.blackUsername(), game.gameName(), game.game());
                } else {
                    throw new InvalidGameException("White player already exists");
                }
            } else if (Objects.equals(playerColor, "BLACK")) {
                if (game.blackUsername() == null) {
                    updatedGame = new GameData(game.gameId(), game.whiteUsername(), verifiedAuth.userName(), game.gameName(), game.game());
                } else {
                    throw new InvalidGameException("Black player already exists");
                }
            } else {
                throw new InvalidCredentialsException("Team color must be WHITE or BLACk");
            }

            gameDAO.joinGame(updatedGame);
        } catch (DataAccessException e) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
    }

    public Collection<ListGamesResult> listGames(String authToken) throws InvalidAuthTokenException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);
            if (verifiedAuth == null) {
                throw new InvalidAuthTokenException("Invalid auth token");
            }
            Collection<ListGamesResult> games = gameDAO.getGames();
            return games;
        } catch (DataAccessException e) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
    }

    public class InvalidAuthTokenException extends Exception {
        public InvalidAuthTokenException(String message) {
            super(message);
        }
    }

    public class InvalidGameException extends Exception {
        public InvalidGameException(String message) {
            super(message);
        }
    }

    public class InvalidCredentialsException extends Exception {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public class InvalidGameRequestException extends Exception {
        public InvalidGameRequestException(String message) {
            super(message);
        }
    }

}
