package dataaccess;

import model.AuthData;

public interface AuthTokenDAO {
    void createAuth(AuthData authData);
    void clearAuth();
}
