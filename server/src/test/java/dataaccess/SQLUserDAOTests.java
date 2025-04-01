package dataaccess;

import dataaccess.mysql.MySQLUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import model.UserData;

import java.util.Collection;

public class SQLUserDAOTests {
    private MySQLUserDAO mySQLUserDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mySQLUserDAO = new MySQLUserDAO();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        mySQLUserDAO.clear();
    }

    @Test
    public void positiveTestCreateUser() throws DataAccessException {
        UserData userData = new UserData("testuser", "password", "test@example.com");
        mySQLUserDAO.createUser(userData);
        Assertions.assertNotNull(mySQLUserDAO.getUser("testuser"));
    }

    @Test
    public void negativeTestCreateUser() throws DataAccessException {
        UserData userData = new UserData("testuser", "password", "test@example.com");
        mySQLUserDAO.createUser(userData);
        Assertions.assertThrows(DataAccessException.class, () -> mySQLUserDAO.createUser(userData));
    }
    
    @Test
    public void positiveTestGetUser() throws DataAccessException {
        UserData userData = new UserData("testuser", "password", "test@example.com");
        mySQLUserDAO.createUser(userData);
        UserData user = mySQLUserDAO.getUser("testuser");
        Assertions.assertEquals(userData.username(), user.username());
        Assertions.assertEquals(userData.email(), user.email());
    }

    @Test
    public void negativeTestGetUser() throws DataAccessException {
        Assertions.assertNull(mySQLUserDAO.getUser("nonexistentuser"));
    }
    
//    @Test
//    public void positiveTestClear() throws DataAccessException {
//        UserData userData = new UserData("testuser", "password", "test@example.com");
//        mySQLUserDAO.createUser(userData);
//        mySQLUserDAO.clear();
//        Assertions.assertNull(mySQLUserDAO.getUser("testuser"));
//    }

    @Test
    public void negativeTestClear() throws DataAccessException {
        mySQLUserDAO.clear();
        Assertions.assertNull(mySQLUserDAO.getUser("testuser"));
        Assertions.assertNull(mySQLUserDAO.getUser("testuser1"));
    }

    @Test
    public void positiveTestGetAllUsers() throws DataAccessException {
        UserData userData1 = new UserData("testuser1", "password1", "test1@example.com");
        UserData userData2 = new UserData("testuser2", "password2", "test2@example.com");
        mySQLUserDAO.createUser(userData1);
        mySQLUserDAO.createUser(userData2);
        Collection<UserData> users = mySQLUserDAO.getAllUsers();
        Assertions.assertEquals(2, users.size());
        
        boolean user1 = false;
        boolean user2 = false;
        for (UserData user : users) {
            if (user.username().equals("testuser1") && user.email().equals("test1@example.com")) {
                user1 = true;
            } else if (user.username().equals("testuser2") && user.email().equals("test2@example.com")) {
                user2 = true;
            }

        }
        Assertions.assertTrue(user1, "User 1 should be in the results");
        Assertions.assertTrue(user2, "User 2 should be in the results");
    }

    @Test
    public void negativeTestGetAllUsers() throws DataAccessException {
        Collection<UserData> users = mySQLUserDAO.getAllUsers();
        Assertions.assertEquals(0, users.size());
    }

}
