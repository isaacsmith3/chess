package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
import types.AuthResult;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class UserService {

    private UserDAO userDAO;
    private AuthTokenDAO authTokenDAO;

    public UserService(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.userDAO = userDAO;
        this.authTokenDAO = authTokenDAO;
    }

    public AuthResult register(UserData request) throws DuplicateUserException {

         try {
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
        } catch (SQLException | DataAccessException e) {
             throw new DuplicateUserException(e.getMessage());
         }
    }

    public AuthResult login(UserData request) throws InvalidCredentialsException {
         try {
            if (request.username() == null || request.password() == null) {
                throw new IllegalArgumentException("Missing required fields");
            }

            UserData existingUser = userDAO.getUser(request.username());

            // This may break for memoryDAO
            if (!verifyPassword(request.password(), existingUser) ) {
                throw new InvalidCredentialsException("Invalid password");
            }

            if (existingUser == null) {
                throw new InvalidCredentialsException("Username not found");
            }

            // For memoryDAO if (!request.password().equals(existingUser.password())
             // (continued) { throw new InvalidCredentialsException("Passwords do not match"); }

            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, request.username());

            authTokenDAO.createAuth(authData);

            return new AuthResult(request.username(), authData.authToken());
        } catch (SQLException | DataAccessException e) {
             throw new InvalidCredentialsException(e.getMessage());
         }

    }

    public void logout(String auth) throws InvalidCredentialsException {
        try {
            String removedAuth = authTokenDAO.deleteAuth(auth);
            if (removedAuth == null) {
                throw new InvalidCredentialsException("Auth token does not exist");
            }
        } catch (DataAccessException e) {
            throw new InvalidCredentialsException(e.getMessage());
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

    public boolean verifyPassword(String rawPassword, UserData storedUser) {
        if (storedUser == null) {
            return false;
        };
        return BCrypt.checkpw(rawPassword, storedUser.password());
    }

}
