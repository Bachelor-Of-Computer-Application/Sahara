package com.sahara.dao;

import com.sahara.model.Caregiver;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaregiverDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Insert a new caregiver
    // ─────────────────────────────────────────────
    public boolean createCaregiver(Caregiver caregiver) {
        String sql = "INSERT INTO caregivers (user_id, gender, age, address, " +
                "experience_years, bio, is_verified, avg_rating) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiver.getUserId());
            stmt.setString(2, caregiver.getGender());
            stmt.setInt(3, caregiver.getAge());
            stmt.setString(4, caregiver.getAddress());
            stmt.setInt(5, caregiver.getExperienceYears());
            stmt.setString(6, caregiver.getBio());
            stmt.setBoolean(7, false); // always false on registration
            stmt.setDouble(8, 0.00);  // always 0 on registration

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error creating caregiver: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // READ — Get caregiver by caregiver_id
    // ─────────────────────────────────────────────
    public Caregiver getCaregiverById(int caregiverId) {
        String sql = "SELECT * FROM caregivers WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractCaregiver(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting caregiver: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // READ — Get caregiver by user_id
    // (used right after caregiver logs in)
    // ─────────────────────────────────────────────
    public Caregiver getCaregiverByUserId(int userId) {
        String sql = "SELECT * FROM caregivers WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractCaregiver(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting caregiver by userId: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // READ — Get all caregivers (for admin panel)
    // ─────────────────────────────────────────────
    public List<Caregiver> getAllCaregivers() {
        List<Caregiver> caregivers = new ArrayList<>();
        String sql = "SELECT * FROM caregivers";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                caregivers.add(extractCaregiver(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all caregivers: " + e.getMessage());
        }
        return caregivers;
    }

    // ─────────────────────────────────────────────
    // READ — Get only verified caregivers
    // (for patient browsing screen)
    // ─────────────────────────────────────────────
    public List<Caregiver> getVerifiedCaregivers() {
        List<Caregiver> caregivers = new ArrayList<>();
        String sql = "SELECT * FROM caregivers WHERE is_verified = true";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                caregivers.add(extractCaregiver(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting verified caregivers: " + e.getMessage());
        }
        return caregivers;
    }

    public List<Caregiver> getVerifiedCaregiversByTier(int tierId) {
        List<Caregiver> caregivers = new ArrayList<>();
        String sql = "SELECT c.* FROM caregivers c " +
                "JOIN caregiver_tiers ct ON c.caregiver_id = ct.caregiver_id " +
                "WHERE c.is_verified = true AND ct.tier_id = ? " +
                "AND ct.is_qualified = true";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, tierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                caregivers.add(extractCaregiver(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting caregivers by tier: " + e.getMessage());
        }
        return caregivers;
    }

    public List<String> getQualifiedTierNames(int caregiverId) {
        List<String> tiers = new ArrayList<>();
        String sql = "SELECT t.tier_name FROM caregiver_tiers ct " +
                "JOIN care_tiers t ON ct.tier_id = t.tier_id " +
                "WHERE ct.caregiver_id = ? AND ct.is_qualified = true " +
                "ORDER BY t.tier_name";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tiers.add(rs.getString("tier_name"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting caregiver tiers: " + e.getMessage());
        }
        return tiers;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Update caregiver profile details
    // ─────────────────────────────────────────────
    public boolean updateCaregiver(Caregiver caregiver) {
        String sql = "UPDATE caregivers SET gender=?, age=?, address=?, " +
                "experience_years=?, bio=? " +
                "WHERE caregiver_id=?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, caregiver.getGender());
            stmt.setInt(2, caregiver.getAge());
            stmt.setString(3, caregiver.getAddress());
            stmt.setInt(4, caregiver.getExperienceYears());
            stmt.setString(5, caregiver.getBio());
            stmt.setInt(6, caregiver.getCaregiverId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating caregiver: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAverageRating(int caregiverId, double avgRating) {
        String sql = "UPDATE caregivers SET avg_rating = ? WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setDouble(1, avgRating);
            stmt.setInt(2, caregiverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating caregiver rating: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Verify a caregiver (admin action)
    // Sets is_verified = true
    // ─────────────────────────────────────────────
    public boolean verifyCaregiver(int caregiverId) {
        String sql = "UPDATE caregivers SET is_verified = true " +
                "WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error verifying caregiver: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // DELETE — Delete caregiver by caregiver_id
    // ─────────────────────────────────────────────
    public boolean deleteCaregiver(int caregiverId) {
        String sql = "DELETE FROM caregivers WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting caregiver: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // HELPER — Convert ResultSet row to Caregiver object
    // ─────────────────────────────────────────────
    private Caregiver extractCaregiver(ResultSet rs) throws SQLException {
        Caregiver caregiver = new Caregiver();
        caregiver.setCaregiverId(rs.getInt("caregiver_id"));
        caregiver.setUserId(rs.getInt("user_id"));
        caregiver.setGender(rs.getString("gender"));
        caregiver.setAge(rs.getInt("age"));
        caregiver.setAddress(rs.getString("address"));
        caregiver.setExperienceYears(rs.getInt("experience_years"));
        caregiver.setBio(rs.getString("bio"));
        caregiver.setVerified(rs.getBoolean("is_verified"));
        caregiver.setAvgRating(rs.getDouble("avg_rating"));
        return caregiver;
    }
}
