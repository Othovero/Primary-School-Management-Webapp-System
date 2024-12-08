package com.studentinformationmanagementsystem.dao;


import com.studentinformationmanagementsystem.model.User;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {

    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public void createUser(User user, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password, user_type, email, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Hash the password before storing
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getUserType());
            stmt.setString(4, user.getEmail());
            stmt.setBoolean(5, user.isStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, user_type = ?, email = ?, status = ? WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getUserType());
            stmt.setString(3, user.getEmail());
            stmt.setBoolean(4, user.isStatus());
            stmt.setInt(5, user.getUserId());

            stmt.executeUpdate();
        }
    }

    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }

    public String generateRememberToken(int userId) throws SQLException {
        String token = UUID.randomUUID().toString();
        String sql = "UPDATE users SET remember_token = ? WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
        return token;
    }

    public boolean validateRememberToken(String username, String token) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ? AND remember_token = ? AND status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, token);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void clearRememberToken(int userId) throws SQLException {
        String sql = "UPDATE users SET remember_token = NULL WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    public boolean isEmailTaken(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setUserType(rs.getString("user_type"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setRememberToken(rs.getString("remember_token"));
        user.setStatus(rs.getBoolean("status"));
        return user;
    }
    // Add these methods to your UserDAO class

    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public void beginTransaction() throws SQLException {
        Connection conn = DatabaseUtil.getConnection();
        conn.setAutoCommit(false);
        connectionHolder.set(conn);
    }

    public void commitTransaction() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                conn.setAutoCommit(true);
                conn.close();
                connectionHolder.remove();
            }
        }
    }

    public void rollbackTransaction() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                connectionHolder.remove();
            }
        }
    }
}