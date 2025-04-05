package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import types.CreateGameResult;
import types.JoinGameRequest;
import types.ListGamesResult;
import types.UpdateGameRequest;
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

    public Boolean verifyAuthToken(String authToken) throws InvalidAuthTokenException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);

            if (verifiedAuth == null) {
                return false;
            }
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinGame(String authToken, JoinGameRequest gameRequest)
            throws InvalidAuthTokenException, InvalidGameException, InvalidCredentialsException, InvalidGameRequestException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);
            String playerColor = gameRequest.playerColor();

            if (verifiedAuth == null) {
                throw new InvalidAuthTokenException("Invalid auth token (join)");
            }

            GameData game = gameDAO.getGame(gameRequest);

            if (game == null) {
                throw new InvalidGameRequestException("Game does not exist (join)");
            }

            GameData updatedGame = getGameData(playerColor, game, verifiedAuth);

            gameDAO.joinGame(updatedGame);
        } catch (DataAccessException e) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
    }

    private GameData getGameData(String playerColor, GameData game, AuthData verifiedAuth) throws InvalidGameException, InvalidCredentialsException {
        GameData updatedGame;

        if (Objects.equals(playerColor, "WHITE")) {
            if (game.whiteUsername() == null) {
                updatedGame = new GameData(game.gameID(), verifiedAuth.userName(), game.blackUsername(), game.gameName(), game.game());
            } else {
                throw new InvalidGameException("White player already exists");
            }
        } else if (Objects.equals(playerColor, "BLACK")) {
            if (game.blackUsername() == null) {
                updatedGame = new GameData(game.gameID(), game.whiteUsername(), verifiedAuth.userName(), game.gameName(), game.game());
            } else {
                throw new InvalidGameException("Black player already exists");
            }
        }
        else {
            throw new InvalidCredentialsException("Team color must be WHITE or BLACk");
        }
        return updatedGame;
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

    public GameData getGame(JoinGameRequest gameRequest) throws InvalidAuthTokenException {
        return gameDAO.getGame(gameRequest);
    }

    public void updateGame(String authToken, UpdateGameRequest gameRequest)
            throws InvalidAuthTokenException, InvalidGameException, InvalidCredentialsException, InvalidGameRequestException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);
            String playerColor = gameRequest.playerColor();

            if (verifiedAuth == null) {
                throw new InvalidAuthTokenException("Invalid auth token (update)");
            }

            JoinGameRequest joinGameRequest = new JoinGameRequest(gameRequest.gameID(), gameRequest.playerColor());

            GameData game = gameDAO.getGame(joinGameRequest);

            if (gameRequest.game() != null) {
                game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameRequest.game());
            }

            if (game == null) {
                throw new InvalidGameRequestException("Game does not exist (update)");
            }

            GameData updatedGame = getData(playerColor, game, verifiedAuth);

            gameDAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
    }

    private GameData getData(String playerColor, GameData game, AuthData verifiedAuth) throws InvalidGameException, InvalidCredentialsException {
        GameData updatedGame;

        if (Objects.equals(playerColor, "WHITE")) {
            if (game.whiteUsername() == null) {
                updatedGame = new GameData(game.gameID(), verifiedAuth.userName(), game.blackUsername(), game.gameName(), game.game());
            } else {
                throw new InvalidGameException("White player already exists");
            }
        } else if (Objects.equals(playerColor, "BLACK")) {
            if (game.blackUsername() == null) {
                updatedGame = new GameData(game.gameID(), game.whiteUsername(), verifiedAuth.userName(), game.gameName(), game.game());
            } else {
                throw new InvalidGameException("Black player already exists");
            }
        } else if (playerColor == null) {
            // This might cause issues with previous code. Here I'm just updating the game through the websocket
            updatedGame = game;
        }
        else {
            throw new InvalidCredentialsException("Team color must be WHITE or BLACk");
        }
        return updatedGame;
    }

    public void leaveGame(String authToken, UpdateGameRequest gameRequest)
            throws InvalidAuthTokenException, InvalidGameException, InvalidCredentialsException, InvalidGameRequestException {
        try {
            AuthData verifiedAuth = authTokenDAO.verifyAuth(authToken);
            String playerColor = gameRequest.playerColor();

            if (verifiedAuth == null) {
                throw new InvalidAuthTokenException("Invalid auth token (leave)");
            }

            JoinGameRequest joinGameRequest = new JoinGameRequest(gameRequest.gameID(), gameRequest.playerColor());

            GameData game = gameDAO.getGame(joinGameRequest);

            if (gameRequest.game() != null) { // For the case where we are updating the game too
                game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameRequest.game());
            }

            if (game == null) {
                throw new InvalidGameRequestException("Game does not exist (leave)");
            }

            GameData updatedGame;

            if (Objects.equals(playerColor, "WHITE")) {
                    updatedGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            } else if (Objects.equals(playerColor, "BLACK")) {
                    updatedGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            } else if (playerColor == null) {
                // This might cause issues with previous code. Here I'm just updating the game through the websocket
                updatedGame = game;
            }
            else {
                throw new InvalidCredentialsException("Team color must be WHITE or BLACk");
            }

            gameDAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
    }

    public AuthData getUserNameByAuthToken(String authToken) throws InvalidAuthTokenException {
        Object authDataCollection = authTokenDAO.getAuthTokens();
        if (authDataCollection == null) {
            throw new InvalidAuthTokenException("No auth token found");
        }
        AuthData authData = null;
        for (var a : (Collection<AuthData>) authDataCollection) {
            if (Objects.equals(a.authToken(), authToken)) {
                authData = new AuthData(a.authToken(), a.userName());
            }
        }
        if (authData == null) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }
        return authData;
    }

    public static class InvalidAuthTokenException extends Exception {
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
