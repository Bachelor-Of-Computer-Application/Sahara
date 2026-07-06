package com.sahara.dao;

import com.sahara.model.Caregiver;
import com.sahara.model.CaregiverView;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaregiverDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

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
            stmt.setBoolean(7, false);
            stmt.setDouble(8, 0.00);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating caregiver: " + e.getMessage());
        }
        return false;
    }

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
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating caregiver: " + e.getMessage());
        }
        return false;
    }

    public boolean verifyCaregiver(int caregiverId) {
        String sql = "UPDATE caregivers SET is_verified = true WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error verifying caregiver: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCaregiver(int caregiverId) {
        String sql = "DELETE FROM caregivers WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting caregiver: " + e.getMessage());
        }
        return false;
    }

    public List<CaregiverView> getAllCaregiversWithNames() {
        List<CaregiverView> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name FROM caregivers c " +
                "JOIN users u ON c.user_id = u.user_id";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Caregiver c = extractCaregiver(rs);
                String fullName = rs.getString("full_name");
                list.add(new CaregiverView(c, fullName));
            }
        } catch (SQLException e) {
            System.out.println("Error getting caregivers with names: " + e.getMessage());
        }
        return list;
    }

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