package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO {

    private Collection<UserData> userDataCollection;

    public MemoryUserDAO() {
        userDataCollection = new ArrayList<>();
    }

    @Override
    public void createUser(UserData userData) {
        userDataCollection.add(userData);
    }

    @Override
    public UserData getUser(String userName) {
        for (UserData user : userDataCollection) {
            if (user.username().equals(userName)) {
                return user;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "MemoryUserDAO{" +
                "userDataCollection=" + userDataCollection +
                '}';
    }
}
