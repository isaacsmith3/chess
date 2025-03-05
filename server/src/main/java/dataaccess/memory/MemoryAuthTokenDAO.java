package dataaccess.memory;

import dataaccess.AuthTokenDAO;
import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthTokenDAO implements AuthTokenDAO {

    private Collection<AuthData> authDataCollection;

    @Override
    public Collection<AuthData> getAuthTokens() {
        return authDataCollection;
    }

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
    public AuthData verifyAuth(String auth) {
        for (AuthData authData : authDataCollection) {
            if (authData.authToken().equals(auth)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        authDataCollection.clear();
    }
}
