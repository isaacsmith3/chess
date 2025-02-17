package dataaccess;

// Have all the data access code that communicate with the database

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData);
    UserData getUser(String userName);
    void clear();
}
