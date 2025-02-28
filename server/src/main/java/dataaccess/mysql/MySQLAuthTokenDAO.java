package dataaccess.mysql;

import dataaccess.AuthTokenDAO;
import model.AuthData;

public class MySQLAuthTokenDAO implements AuthTokenDAO {

    public DatabaseManager databaseManager = new DatabaseManager();

    public MySQLAuthTokenDAO() {

    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public void clearAuth() {

    }

    @Override
    public String deleteAuth(String auth) {
        return "";
    }

    @Override
    public AuthData verifyAuth(String auth) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public Object getAuthDataCollection() {
        return null;
    }
}
