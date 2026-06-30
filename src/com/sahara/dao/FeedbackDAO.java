package com.sahara.dao;

import com.sahara.model.Feedback;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // CREATE — Patient submits feedback
    // ─────────────────────────────────────────────
    public boolean createFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback (booking_id, patient_id, " +
                "caregiver_id, rating, review) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, feedback.getBookingId());
            stmt.setInt(2, feedback.getPatientId());
            stmt.setInt(3, feedback.getCaregiverId());
            stmt.setInt(4, feedback.getRating());
            stmt.setString(5, feedback.getReview());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating feedback: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // READ — Get feedback by booking ID
    // ─────────────────────────────────────────────
    public Feedback getFeedbackByBookingId(int bookingId) {
        String sql = "SELECT * FROM feedback WHERE booking_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractFeedback(rs);
        } catch (SQLException e) {
            System.out.println("Error getting feedback: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // READ — Get all feedback for a caregiver
    // ─────────────────────────────────────────────
    public List<Feedback> getFeedbackByCaregiverId(int caregiverId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(extractFeedback(rs));
        } catch (SQLException e) {
            System.out.println("Error getting caregiver feedback: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────
    // READ — Get average rating for a caregiver
    // ─────────────────────────────────────────────
    public double getAverageRating(int caregiverId) {
        String sql = "SELECT AVG(rating) FROM feedback WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println("Error getting average rating: " + e.getMessage());
        }
        return 0.0;
    }

    // ─────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────
    private Feedback extractFeedback(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setFeedbackId(rs.getInt("feedback_id"));
        f.setBookingId(rs.getInt("booking_id"));
        f.setPatientId(rs.getInt("patient_id"));
        f.setCaregiverId(rs.getInt("caregiver_id"));
        f.setRating(rs.getInt("rating"));
        f.setReview(rs.getString("review"));
        Timestamp ts = rs.getTimestamp("submitted_at");
        if (ts != null) f.setSubmittedAt(ts.toLocalDateTime());
        return f;
    }
}