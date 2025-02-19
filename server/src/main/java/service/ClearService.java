package service;

import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;
    private GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthTokenDAO authTokenDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authTokenDAO = authTokenDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        userDAO.clear();
        authTokenDAO.clear();
        gameDAO.clear();
    }

}
