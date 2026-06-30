package com.sahara.dao;

import com.sahara.model.Hospital;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Add a new hospital
    // ─────────────────────────────────────────────
    public boolean createHospital(Hospital hospital) {
        String sql = "INSERT INTO hospitals (name, address, city) " +
                "VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, hospital.getName());
            stmt.setString(2, hospital.getAddress());
            stmt.setString(3, hospital.getCity());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error creating hospital: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // READ — Get hospital by ID
    // ─────────────────────────────────────────────
    public Hospital getHospitalById(int hospitalId) {
        String sql = "SELECT * FROM hospitals WHERE hospital_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, hospitalId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractHospital(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting hospital: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // READ — Get all hospitals
    // ─────────────────────────────────────────────
    public List<Hospital> getAllHospitals() {
        List<Hospital> hospitals = new ArrayList<>();
        String sql = "SELECT * FROM hospitals";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hospitals.add(extractHospital(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting hospitals: " + e.getMessage());
        }
        return hospitals;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Update hospital details
    // ─────────────────────────────────────────────
    public boolean updateHospital(Hospital hospital) {
        String sql = "UPDATE hospitals SET name=?, address=?, city=? " +
                "WHERE hospital_id=?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, hospital.getName());
            stmt.setString(2, hospital.getAddress());
            stmt.setString(3, hospital.getCity());
            stmt.setInt(4, hospital.getHospitalId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating hospital: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // DELETE — Delete hospital
    // ─────────────────────────────────────────────
    public boolean deleteHospital(int hospitalId) {
        String sql = "DELETE FROM hospitals WHERE hospital_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, hospitalId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting hospital: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // HELPER — Convert ResultSet row to Hospital object
    // ─────────────────────────────────────────────
    private Hospital extractHospital(ResultSet rs) throws SQLException {
        Hospital hospital = new Hospital();
        hospital.setHospitalId(rs.getInt("hospital_id"));
        hospital.setName(rs.getString("name"));
        hospital.setAddress(rs.getString("address"));
        hospital.setCity(rs.getString("city"));
        return hospital;
    }
}