package dataaccess;

// Have all the data access code that communicate with the database

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String userName);
    void clear();
    Collection<UserData> getAllUsers();
}
