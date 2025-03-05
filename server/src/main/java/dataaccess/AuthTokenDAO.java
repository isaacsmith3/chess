package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthTokenDAO {
    void createAuth(AuthData authData) throws DataAccessException, SQLException;
    String deleteAuth(String auth) throws DataAccessException;
    AuthData verifyAuth(String auth);
    void clear();
    Object getAuthTokens();
}
