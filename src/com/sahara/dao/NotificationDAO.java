package com.sahara.dao;

import com.sahara.model.Notification;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Send a notification to a user
    // ─────────────────────────────────────────────
    public boolean createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, message, is_read) " +
                "VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setBoolean(3, false);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating notification: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // READ — Get all notifications for a user
    // (most recent first)
    // ─────────────────────────────────────────────
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? " +
                "ORDER BY created_at DESC";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setNotificationId(rs.getInt("notification_id"));
                n.setUserId(rs.getInt("user_id"));
                n.setMessage(rs.getString("message"));
                n.setRead(rs.getBoolean("is_read"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) n.setCreatedAt(ts.toLocalDateTime());
                list.add(n);
            }
        } catch (SQLException e) {
            System.out.println("Error getting notifications: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    // READ — Count unread notifications for a user
    // ─────────────────────────────────────────────
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications " +
                "WHERE user_id = ? AND is_read = false";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error counting notifications: " + e.getMessage());
        }
        return 0;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Mark one notification as read
    // ─────────────────────────────────────────────
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = true " +
                "WHERE notification_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error marking notification as read: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Mark ALL notifications as read
    // ─────────────────────────────────────────────
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = true " +
                "WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error marking all notifications: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // DELETE — Delete all notifications for a user
    // ─────────────────────────────────────────────
    public boolean deleteAllNotifications(int userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting notifications: " + e.getMessage());
        }
        return false;
    }
}