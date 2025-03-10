package dataaccess.mysql;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLAuthTokenDAO implements AuthTokenDAO {

    public DatabaseManager databaseManager = new DatabaseManager();

    public MySQLAuthTokenDAO() throws DataAccessException {
        databaseManager.configureDatabase();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        databaseManager.executeUpdate(sql, authData.authToken(), authData.userName());
    }

    @Override
    public String deleteAuth(String auth) throws DataAccessException {
        try (Connection conn = databaseManager.getConnection()) {
            var sql = "DELETE FROM auths WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, auth);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Error: unauthorized");
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public AuthData verifyAuth(String auth) throws DataAccessException {
        try (Connection conn = databaseManager.getConnection()) {
            var sql = "SELECT * FROM auths WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, auth);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                    return null;
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            var sql = "DELETE FROM auths";
            databaseManager.executeUpdate(sql);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getAuthTokens() {
        try (Connection conn = databaseManager.getConnection()) {
            var sql = "SELECT * FROM auths";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    List<AuthData> auths = new ArrayList<>();
                    while (rs.next()) {
                        auths.add(new AuthData(rs.getString("authToken"), rs.getString("username")));
                    }
                    return auths;
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
