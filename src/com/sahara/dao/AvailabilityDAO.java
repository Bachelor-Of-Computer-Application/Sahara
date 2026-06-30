package com.sahara.dao;

import com.sahara.model.Availability;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Caregiver adds availability
    // ─────────────────────────────────────────────
    public boolean addAvailability(Availability availability) {
        String sql = "INSERT INTO availability " +
                "(caregiver_id, available_date, is_available) " +
                "VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, availability.getCaregiverId());
            stmt.setDate(2, Date.valueOf(availability.getAvailableDate()));
            stmt.setBoolean(3, availability.isAvailable());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding availability: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // READ — Get all availability for a caregiver
    // ─────────────────────────────────────────────
    public List<Availability> getAvailabilityByCaregiverId(int caregiverId) {
        List<Availability> list = new ArrayList<>();
        String sql = "SELECT * FROM availability WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Availability a = new Availability();
                a.setAvailabilityId(rs.getInt("availability_id"));
                a.setCaregiverId(rs.getInt("caregiver_id"));
                a.setAvailableDate(rs.getDate("available_date").toLocalDate());
                a.setAvailable(rs.getBoolean("is_available"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error getting availability: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Toggle availability on/off
    // ─────────────────────────────────────────────
    public boolean updateAvailability(int availabilityId, boolean isAvailable) {
        String sql = "UPDATE availability SET is_available = ? " +
                "WHERE availability_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, availabilityId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating availability: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // DELETE — Remove an availability entry
    // ─────────────────────────────────────────────
    public boolean deleteAvailability(int availabilityId) {
        String sql = "DELETE FROM availability WHERE availability_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, availabilityId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting availability: " + e.getMessage());
        }
        return false;
    }
}