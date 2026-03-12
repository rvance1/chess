package dataaccess.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import exception.DataAccessException;
import model.UserData;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.email());

            ps.executeUpdate();
        }catch (SQLException ex) {
            throw new DataAccessException("failed to insert user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}