package com.sahara.dao;

import com.sahara.model.Booking;
import com.sahara.model.BookingView;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public int createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (patient_id, caregiver_id, tier_id, " +
                "hospital_id, ward, admission_date, discharge_date, " +
                "total_days, total_cost, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, booking.getPatientId());
            stmt.setInt(2, booking.getCaregiverId());
            stmt.setInt(3, booking.getTierId());
            stmt.setInt(4, booking.getHospitalId());
            stmt.setString(5, booking.getWard());
            stmt.setDate(6, Date.valueOf(booking.getAdmissionDate()));
            stmt.setDate(7, Date.valueOf(booking.getDischargeDate()));
            stmt.setInt(8, booking.getTotalDays());
            stmt.setDouble(9, booking.getTotalCost());
            stmt.setString(10, "PENDING");
            stmt.setString(11, booking.getNotes());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error creating booking: " + e.getMessage());
        }
        return -1;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractBooking(rs);
        } catch (SQLException e) {
            System.out.println("Error getting booking: " + e.getMessage());
        }
        return null;
    }

    public List<Booking> getBookingsByPatientId(int patientId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE patient_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(extractBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error getting patient bookings: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> getBookingsByCaregiverId(int caregiverId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE caregiver_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(extractBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error getting caregiver bookings: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> getBookingsByCaregiverIdAndStatus(int caregiverId, String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE caregiver_id = ? AND status = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, caregiverId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(extractBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error getting bookings by caregiver and status: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> getBookingsByPatientIdAndStatus(int patientId, String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE patient_id = ? AND status = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setString(2, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(extractBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error getting bookings by patient and status: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(extractBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error getting all bookings: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) bookings.add(extractBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error getting bookings by status: " + e.getMessage());
        }
        return bookings;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating booking status: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET ward=?, admission_date=?, " +
                "discharge_date=?, total_days=?, total_cost=?, notes=? " +
                "WHERE booking_id=?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, booking.getWard());
            stmt.setDate(2, Date.valueOf(booking.getAdmissionDate()));
            stmt.setDate(3, Date.valueOf(booking.getDischargeDate()));
            stmt.setInt(4, booking.getTotalDays());
            stmt.setDouble(5, booking.getTotalCost());
            stmt.setString(6, booking.getNotes());
            stmt.setInt(7, booking.getBookingId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating booking: " + e.getMessage());
        }
        return false;
    }

    public List<BookingView> getAllBookingsWithNames() {
        List<BookingView> list = new ArrayList<>();
        String sql = "SELECT b.*, " +
                "pu.full_name AS patient_name, " +
                "cu.full_name AS caregiver_name, " +
                "h.name AS hospital_name " +
                "FROM bookings b " +
                "JOIN patients p ON b.patient_id = p.patient_id " +
                "JOIN users pu ON p.user_id = pu.user_id " +
                "JOIN caregivers c ON b.caregiver_id = c.caregiver_id " +
                "JOIN users cu ON c.user_id = cu.user_id " +
                "JOIN hospitals h ON b.hospital_id = h.hospital_id";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = extractBooking(rs);
                String patientName   = rs.getString("patient_name");
                String caregiverName = rs.getString("caregiver_name");
                String hospitalName  = rs.getString("hospital_name");
                list.add(new BookingView(booking, patientName,
                        caregiverName, hospitalName));
            }
        } catch (SQLException e) {
            System.out.println("Error getting bookings with names: " + e.getMessage());
        }
        return list;
    }

    private Booking extractBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setPatientId(rs.getInt("patient_id"));
        booking.setCaregiverId(rs.getInt("caregiver_id"));
        booking.setTierId(rs.getInt("tier_id"));
        booking.setHospitalId(rs.getInt("hospital_id"));
        booking.setWard(rs.getString("ward"));
        Date admDate = rs.getDate("admission_date");
        if (admDate != null) booking.setAdmissionDate(admDate.toLocalDate());
        Date disDate = rs.getDate("discharge_date");
        if (disDate != null) booking.setDischargeDate(disDate.toLocalDate());
        booking.setTotalDays(rs.getInt("total_days"));
        booking.setTotalCost(rs.getDouble("total_cost"));
        booking.setStatus(rs.getString("status"));
        booking.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("booked_at");
        if (ts != null) booking.setBookedAt(ts.toLocalDateTime());
        return booking;
    }
}