package dao;

import exception.DatabaseException;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for 'users' table.
 */
public class UserDAO {

    /**
     * Registers a new user in the database.
     */
    public boolean register(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, password, email, full_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullName());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register user: " + e.getMessage(), e);
        }
    }

    /**
     * Authenticates a user using username and password hash.
     */
    public User login(String username, String password) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during login: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Updates password hash for a specific user ID.
     */
    public boolean changePassword(int userId, String newPassword) throws DatabaseException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to change password: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if username is already taken.
     */
    public boolean checkUsernameExists(String username) throws DatabaseException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check username availability: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if email is already registered.
     */
    public boolean checkEmailExists(String email) throws DatabaseException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check email availability: " + e.getMessage(), e);
        }
    }
}
