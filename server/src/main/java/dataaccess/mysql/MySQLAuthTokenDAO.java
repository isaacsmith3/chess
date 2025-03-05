package dataaccess.mysql;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLAuthTokenDAO implements AuthTokenDAO {

    public DatabaseManager databaseManager = new DatabaseManager();

    public MySQLAuthTokenDAO() throws DataAccessException {
        databaseManager.configureDatabase();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var SQLStatement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        databaseManager.executeUpdate(SQLStatement, authData.authToken(), authData.userName());
    }

    @Override
    public void clearAuth() {

    }

    @Override
    public String deleteAuth(String auth) throws DataAccessException {
//        try (Connection conn = databaseManager.getConnection()) {
//            var SQLStatement = "DELETE FROM auth_token WHERE user_id = ? AND token = ?";
//
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage());
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }

        return "";
    }

    @Override
    public AuthData verifyAuth(String auth) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public Object getAuthTokens() {
        return null;
    }
}
