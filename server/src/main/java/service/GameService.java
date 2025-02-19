package service;

import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import endpoint.GameResult;

public class GameService {
    private GameDAO gameDAO;
    private AuthTokenDAO authTokenDAO;

    public GameService(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameDAO = gameDAO;
        this.authTokenDAO = authTokenDAO;
    }



    public GameResult createGame(String authToken, String gameName) throws InvalidAuthTokenException {
        boolean verifiedAuth = authTokenDAO.verifyAuth(authToken);
        if (!verifiedAuth) {
            throw new InvalidAuthTokenException("Invalid auth token");
        }

        GameResult gameResult = gameDAO.createGame(gameName);
        return gameResult;
    }

    public class InvalidAuthTokenException extends Exception {
        public InvalidAuthTokenException(String message) {
            super(message);
        }
    }

}
