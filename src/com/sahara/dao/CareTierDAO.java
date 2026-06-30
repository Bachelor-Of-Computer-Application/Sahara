package com.sahara.dao;

import com.sahara.model.CareTier;
import com.sahara.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CareTierDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ─────────────────────────────────────────────
    // READ — Get all care tiers
    // (already inserted in schema.sql)
    // ─────────────────────────────────────────────
    public List<CareTier> getAllTiers() {
        List<CareTier> tiers = new ArrayList<>();
        String sql = "SELECT * FROM care_tiers";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tiers.add(extractCareTier(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting tiers: " + e.getMessage());
        }
        return tiers;
    }

    // ─────────────────────────────────────────────
    // READ — Get one tier by ID
    // ─────────────────────────────────────────────
    public CareTier getTierById(int tierId) {
        String sql = "SELECT * FROM care_tiers WHERE tier_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setInt(1, tierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractCareTier(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting tier: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // UPDATE — Update tier price (admin only)
    // ─────────────────────────────────────────────
    public boolean updateTierPrice(int tierId, double newPrice) {
        String sql = "UPDATE care_tiers SET price_per_day = ? " +
                "WHERE tier_id = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, tierId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating tier price: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // HELPER — Convert ResultSet row to CareTier object
    // ─────────────────────────────────────────────
    private CareTier extractCareTier(ResultSet rs) throws SQLException {
        CareTier tier = new CareTier();
        tier.setTierId(rs.getInt("tier_id"));
        tier.setTierName(rs.getString("tier_name"));
        tier.setDescription(rs.getString("description"));
        tier.setPricePerDay(rs.getDouble("price_per_day"));
        return tier;
    }
}