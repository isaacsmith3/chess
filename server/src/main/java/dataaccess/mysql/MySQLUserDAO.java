package dataaccess.mysql;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class MySQLUserDAO implements UserDAO {

    public final DatabaseManager databaseManager = new DatabaseManager();

    public MySQLUserDAO() throws DataAccessException {
        databaseManager.configureDatabase();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        userData = new UserData(userData.username(), hashedPassword, userData.email());
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        databaseManager.executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String userName) {
        try (Connection conn = databaseManager.getConnection()) {
            var SQLStatement = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(SQLStatement)) {
                ps.setString(1, userName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public Collection<UserData> getAllUsers() {
        return List.of();
    }
}
