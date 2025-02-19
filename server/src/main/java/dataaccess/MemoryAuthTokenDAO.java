package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthTokenDAO implements AuthTokenDAO {

    private Collection<AuthData> authDataCollection;

    public MemoryAuthTokenDAO() {
        this.authDataCollection = new ArrayList<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        authDataCollection.add(authData);
    }

    @Override
    public String toString() {
        return "MemoryAuthTokenDAO{" +
                "authDataCollection=" + authDataCollection +
                '}';
    }

    @Override
    public void clearAuth() {
        authDataCollection.clear();
    }

    @Override
    public String deleteAuth(String auth) {
        for (AuthData authData : authDataCollection) {
            if (authData.authToken().equals(auth)) {
                authDataCollection.remove(authData);
                return authData.authToken();
            }
        }
        return null;
    }

    @Override
    public boolean verifyAuth(String auth) {
        for (AuthData authData : authDataCollection) {
            if (authData.authToken().equals(auth)) {
                return true;
            }
        }
        return false;
    }
}
