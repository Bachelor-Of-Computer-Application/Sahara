package com.sahara.dao;

import com.sahara.model.Admin;
import com.sahara.util.DBConnection;

import java.sql.*;

public class AdminDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Create admin record
    // ─────────────────────────────────────────────
    public boolean createAdmin(Admin admin) {
        String sql = "INSERT INTO admins (user_id) VALUES (?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, admin.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating admin: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // READ — Get admin by user_id
    // ─────────────────────────────────────────────
    public Admin getAdminByUserId(int userId) {
        String sql = "SELECT * FROM admins WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUserId(rs.getInt("user_id"));
                return admin;
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin: " + e.getMessage());
        }
        return null;
    }
}