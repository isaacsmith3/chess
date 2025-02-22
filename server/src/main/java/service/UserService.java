package service;

import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import types.AuthResult;
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

    public AuthResult register(UserData request) throws DuplicateUserException {

        if (request.username() == null || request.password() == null) {
            throw new IllegalArgumentException("Missing required fields");
        }

        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser != null) {
            throw new DuplicateUserException("Username already exists");
        }

        userDAO.createUser(request);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());

        authTokenDAO.createAuth(authData);

        return new AuthResult(request.username(), authData.authToken());
    }

    public AuthResult login(UserData request) throws InvalidCredentialsException {
        if (request.username() == null || request.password() == null) {
            throw new IllegalArgumentException("Missing required fields");
        }

        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser == null) {
            throw new InvalidCredentialsException("Username not found");
        }

        if (!request.password().equals(existingUser.password())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());

        authTokenDAO.createAuth(authData);

        return new AuthResult(request.username(), authData.authToken());

    }

    public void logout(String auth) throws InvalidCredentialsException {
        String removedAuth = authTokenDAO.deleteAuth(auth);
        if (removedAuth == null) {
            throw new InvalidCredentialsException("Auth token does not exist");
        }
    }

    // Custom exceptions
    public class DuplicateUserException extends Exception {
        public DuplicateUserException(String message) {
            super(message);
        }
    }

    public class InvalidCredentialsException extends Exception {
        public InvalidCredentialsException(String message) { super(message); }
    }

}
