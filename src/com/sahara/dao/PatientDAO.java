package com.sahara.dao;

import com.sahara.model.Patient;
import com.sahara.model.PatientView;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public boolean createPatient(Patient patient) {
        String sql = "INSERT INTO patients (user_id, gender, age, address, emergency_contact) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, patient.getUserId());
            stmt.setString(2, patient.getGender());
            stmt.setInt(3, patient.getAge());
            stmt.setString(4, patient.getAddress());
            stmt.setString(5, patient.getEmergencyContact());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating patient: " + e.getMessage());
        }
        return false;
    }

    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractPatient(rs);
        } catch (SQLException e) {
            System.out.println("Error getting patient: " + e.getMessage());
        }
        return null;
    }

    public Patient getPatientByUserId(int userId) {
        String sql = "SELECT * FROM patients WHERE user_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractPatient(rs);
        } catch (SQLException e) {
            System.out.println("Error getting patient by userId: " + e.getMessage());
        }
        return null;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) patients.add(extractPatient(rs));
        } catch (SQLException e) {
            System.out.println("Error getting all patients: " + e.getMessage());
        }
        return patients;
    }

    public List<PatientView> getAllPatientsWithNames() {
        List<PatientView> list = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name, u.email, u.phone " +
                "FROM patients p " +
                "JOIN users u ON p.user_id = u.user_id";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Patient p       = extractPatient(rs);
                String fullName = rs.getString("full_name");
                String email    = rs.getString("email");
                String phone    = rs.getString("phone");
                list.add(new PatientView(p, fullName, email, phone));
            }
        } catch (SQLException e) {
            System.out.println("Error getting patients with names: " + e.getMessage());
        }
        return list;
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET gender=?, age=?, " +
                "address=?, emergency_contact=? WHERE patient_id=?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, patient.getGender());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getEmergencyContact());
            stmt.setInt(5, patient.getPatientId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
        return false;
    }

    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, patientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
        }
        return false;
    }

    private Patient extractPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setUserId(rs.getInt("user_id"));
        patient.setGender(rs.getString("gender"));
        patient.setAge(rs.getInt("age"));
        patient.setAddress(rs.getString("address"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        return patient;
    }
}