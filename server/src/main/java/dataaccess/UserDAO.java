package dataaccess;

// Have all the data access code that communicate with the database

import model.UserData;

public interface UserDAO {
    public void createUser(UserData userData);
    public UserData getUser(String userName);
}
