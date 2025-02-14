package service;

import dataaccess.AuthTokenDAO;
import dataaccess.MemoryAuthTokenDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;


public class UserService {

    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;

    public UserService(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.userDAO = userDAO;
        this.authTokenDAO = authTokenDAO;
    }

    public AuthData register(UserData request) throws DuplicateUserException {

        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser != null) {
            throw new DuplicateUserException("Username already exists");
        }

        userDAO.createUser(request);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());

        authTokenDAO.createAuth(authData);

        return authData;
    }

    // Custom exception
    public class DuplicateUserException extends Exception {
        public DuplicateUserException(String message) {
            super(message);
        }
    }


}
