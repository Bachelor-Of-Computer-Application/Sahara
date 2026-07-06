package com.sahara.dao;

import com.sahara.model.User;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ── Get connection from DBConnection ──────────
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Insert a new user into database
    // ─────────────────────────────────────────────
    public int createUser(User user) {
        String sql = "INSERT INTO users (full_name, email, phone, password_hash, role) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole());

            stmt.executeUpdate();

            // Get the auto generated user_id
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1); // return the new user_id
            }
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
        return -1; // return -1 if failed
    }

    // ─────────────────────────────────────────────
    // READ — Get a user by their ID
    // ─────────────────────────────────────────────
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs); // convert row to User object
            }
        } catch (SQLException e) {
            System.out.println("Error getting user: " + e.getMessage());
        }
        return null; // return null if not found
    }

    // ─────────────────────────────────────────────
    // READ — Get a user by their email (used for login)
    // ─────────────────────────────────────────────
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by email: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // READ — Get all users
    // ─────────────────────────────────────────────
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Update a user's details
    // ─────────────────────────────────────────────
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=? " +
                "WHERE user_id=?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // true if update was successful
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // DELETE — Delete a user by ID
    // ─────────────────────────────────────────────
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // CHECK — Check if email already exists
    // (used during registration)
    // ─────────────────────────────────────────────
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if email exists
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // HELPER — Convert a ResultSet row to a User object
    // Used internally by all READ methods
    // ─────────────────────────────────────────────
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        return user;
    }
    // ─────────────────────────────────────────────
    // UPDATE — Reset just the password (used by "Forgot Password")
    // ─────────────────────────────────────────────
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
        }
        return false;
    }
}
