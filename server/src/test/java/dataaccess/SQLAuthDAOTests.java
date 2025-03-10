package dataaccess;

import dataaccess.mysql.MySQLAuthTokenDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class SQLAuthDAOTests {
    private MySQLAuthTokenDAO mySQLAuthDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mySQLAuthDAO = new MySQLAuthTokenDAO();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        mySQLAuthDAO.clear();
    }

    @Test
    public void positiveTestCreateAuth() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        Collection<AuthData> auths = (Collection<AuthData>) mySQLAuthDAO.getAuthTokens();
        Assertions.assertEquals(1, auths.size());
        Assertions.assertEquals(authData, auths.iterator().next());
    }
    
    @Test
    public void negativeTestCreateAuth() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        Assertions.assertThrows(DataAccessException.class, () -> mySQLAuthDAO.createAuth(authData));
    }

    @Test
    public void positiveTestDeleteAuth() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        mySQLAuthDAO.deleteAuth(authData.authToken());
        Assertions.assertNull(mySQLAuthDAO.verifyAuth(authData.authToken()));
    }

    @Test
    public void negativeTestDeleteAuth() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        Assertions.assertThrows(DataAccessException.class, () -> mySQLAuthDAO.deleteAuth(authData.authToken()));
    }
    
    @Test
    public void positiveTestVerifyAuth() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        Assertions.assertEquals(authData, mySQLAuthDAO.verifyAuth(authData.authToken()));
    }
    
    @Test
    public void negativeTestVerifyAuth() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        Assertions.assertNull(mySQLAuthDAO.verifyAuth("invalidtoken"));
    }

    @Test
    public void positiveTestClear() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        mySQLAuthDAO.clear();
        Assertions.assertNull(mySQLAuthDAO.verifyAuth(authData.authToken()));
    }
    
    @Test
    public void negativeTestClear() throws DataAccessException {
        mySQLAuthDAO.clear();
        Assertions.assertNull(mySQLAuthDAO.verifyAuth("testuser"));
    }

    @Test
    public void positiveTestGetAuthTokens() throws DataAccessException {
        AuthData authData = new AuthData("testuser", "password");
        mySQLAuthDAO.createAuth(authData);
        Collection<AuthData> auths = (Collection<AuthData>) mySQLAuthDAO.getAuthTokens();
        Assertions.assertEquals(1, auths.size());
        Assertions.assertEquals(authData, auths.iterator().next());
    }

    @Test
    public void negativeTestGetAuthTokens() {
        mySQLAuthDAO.clear();
        Assertions.assertEquals(new ArrayList<AuthData>(), mySQLAuthDAO.getAuthTokens());
    }

}
