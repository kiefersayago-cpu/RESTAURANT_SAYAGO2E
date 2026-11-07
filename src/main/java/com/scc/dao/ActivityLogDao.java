
package com.scc.dao;

import com.scc.model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    public void logActivity(int userId, String username, String action) {

        String sql = "INSERT INTO activity_logs " +
                "(user_id, username, action) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, action);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving activity log: " + e.getMessage());
        }
    }

    public List<ActivityLog> getAllLogs() {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = "SELECT * FROM activity_logs ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("action"),
                        rs.getString("timestamp")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
        }

        return logs;
    }

    public List<ActivityLog> getLogsByUserId(int userId) {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = "SELECT * FROM activity_logs " +
                "WHERE user_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("action"),
                            rs.getString("timestamp")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user activity logs: " + e.getMessage());
        }

        return logs;
    }

    public boolean clearLogs() {

        String sql = "DELETE FROM activity_logs";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing activity logs: " + e.getMessage());
            return false;
        }
    }

    public List<ActivityLog> getLogsByPage(int page, int pageSize) {

        List<ActivityLog> logs = new ArrayList<>();

        int offset = (page - 1) * pageSize;

        String sql = "SELECT * FROM activity_logs " +
                "ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("action"),
                            rs.getString("timestamp")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching paginated logs: " + e.getMessage());
        }

        return logs;
    }

    public int getTotalPages(int pageSize) {

        String sql = "SELECT COUNT(*) FROM activity_logs";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return (int) Math.ceil((double) rs.getInt(1) / pageSize);
            }

        } catch (SQLException e) {
            System.err.println("Error counting activity logs: " + e.getMessage());
        }

        return 0;
    }

}
