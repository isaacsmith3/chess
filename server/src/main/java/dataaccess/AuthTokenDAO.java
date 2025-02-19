package dataaccess;

import model.AuthData;

public interface AuthTokenDAO {
    void createAuth(AuthData authData);
    void clearAuth();
    String deleteAuth(String auth);
    AuthData verifyAuth(String auth);
    void clear();
}
