package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthTokenDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTests {

    public UserService userService;
    public UserDAO userDAO = new MemoryUserDAO();
    public AuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();

    @BeforeEach
    public void setUp() {
        this.userService = new UserService(userDAO, authTokenDAO);
    }

    @Test
    public void positiveTestRegister() throws UserService.DuplicateUserException {
        userService.register(new UserData("username", "password", "email"));

        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("username", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.size() == 1);
    }

    @Test public void negativeTestRegister() throws UserService.DuplicateUserException {
        userService.register(new UserData("isaac", "password", "email"));
        assertThrows(UserService.DuplicateUserException.class, () -> {
            userService.register(new UserData("isaac", "password", "email"));
        });
    }

    @Test public void positiveTestLogout() throws UserService.DuplicateUserException, UserService.InvalidCredentialsException {
        userService.register(new UserData("isaac", "password", "email"));
        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("isaac", "password", "email")));

        Collection<AuthData> authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        String auth = String.valueOf(authDataCollection.iterator().next().authToken());

        userService.logout(auth);
        authDataCollection = (Collection<AuthData>) authTokenDAO.getAuthTokens();
        assertTrue(authDataCollection.isEmpty());
    }

    @Test public void negativeTestLogout() throws UserService.InvalidCredentialsException, UserService.DuplicateUserException {
        userService.register(new UserData("isaac", "password", "email"));
        Collection<UserData> userDataCollection = userDAO.getAllUsers();
        assertTrue(userDataCollection.size() == 1);
        assertTrue(userDataCollection.contains(new UserData("isaac", "password", "email")));

        assertThrows(UserService.InvalidCredentialsException.class, () -> {
            userService.logout("auth");
        });

    }


}
