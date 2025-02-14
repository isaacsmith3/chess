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
}
