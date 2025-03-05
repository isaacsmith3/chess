package dataaccess.mysql;

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
    public UserData getUser(String username) {
        try (Connection conn = databaseManager.getConnection()) {
            String sql = "SELECT username, password, email FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                    return null;
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            var SQLStatement = "DELETE FROM users";
            databaseManager.executeUpdate(SQLStatement);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<UserData> getAllUsers() {
        return List.of();
    }
}
