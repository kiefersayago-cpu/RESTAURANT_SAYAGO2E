
package com.scc.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    // CREATE CUSTOMER RESERVATION
    public boolean createReservation(
            int userId,
            String date,
            String time,
            int guests) {

        String sql = """
                INSERT INTO reservations
                (user_id, reservation_date, reservation_time, guests, status)
                VALUES (?, ?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setInt(4, guests);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Reservation error: " + e.getMessage());
            return false;
        }
    }

    // VIEW ALL RESERVATIONS
    public void viewAllReservations() {
        String sql = """
                SELECT r.id, u.username, r.reservation_date,
                       r.reservation_time, r.guests, r.status
                FROM reservations r
                JOIN users u ON r.user_id = u.id
                ORDER BY r.id DESC
                """;

        displayReservations(sql);
    }

    // VIEW CUSTOMER RESERVATIONS
    public void viewMyReservations(int userId) {
        String sql = """
                SELECT r.id, u.username, r.reservation_date,
                       r.reservation_time, r.guests, r.status
                FROM reservations r
                JOIN users u ON r.user_id = u.id
                WHERE r.user_id = ?
                ORDER BY r.id DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                printReservations(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error viewing reservations: " + e.getMessage());
        }
    }

    // VIEW PENDING RESERVATIONS
    public void viewPendingReservations() {
        String sql = """
                SELECT r.id, u.username, r.reservation_date,
                       r.reservation_time, r.guests, r.status
                FROM reservations r
                JOIN users u ON r.user_id = u.id
                WHERE UPPER(r.status) = 'PENDING'
                ORDER BY r.id ASC
                """;

        displayReservations(sql);
    }

    // APPROVE OR REJECT RESERVATION
    public boolean updateReservationStatus(
            int reservationId,
            String newStatus) {

        if (newStatus == null) {
            return false;
        }

        newStatus = newStatus.trim().toUpperCase();

        if (!newStatus.equals("APPROVED")
                && !newStatus.equals("REJECTED")) {
            System.out.println("Invalid reservation status.");
            return false;
        }

        String sql = """
                UPDATE reservations
                SET status = ?
                WHERE id = ?
                AND UPPER(status) = 'PENDING'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, reservationId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating reservation: " + e.getMessage());
            return false;
        }
    }

    // CHECK IF RESERVATION BELONGS TO CUSTOMER
    public boolean ownsReservation(int reservationId, int userId) {

        String sql = """
                SELECT id FROM reservations
                WHERE id = ? AND user_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking reservation: " + e.getMessage());
            return false;
        }
    }

    // DISPLAY RESERVATIONS
    private void displayReservations(String sql) {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            printReservations(rs);

        } catch (SQLException e) {
            System.out.println("Error viewing reservations: " + e.getMessage());
        }
    }

    // PRINT RESERVATION TABLE
    private void printReservations(ResultSet rs) throws SQLException {

        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                         CUSTOMER RESERVATIONS                            |");
        System.out.println("+------+----------------+--------------+--------------+--------+-----------+");
        System.out.printf("| %-4s | %-14s | %-12s | %-12s | %-6s | %-9s |%n",
                "ID", "Customer", "Date", "Time", "Guests", "Status");
        System.out.println("+------+----------------+--------------+--------------+--------+-----------+");

        boolean found = false;

        while (rs.next()) {
            found = true;

            System.out.printf("| %-4d | %-14s | %-12s | %-12s | %-6d | %-9s |%n",
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("reservation_date"),
                    rs.getString("reservation_time"),
                    rs.getInt("guests"),
                    rs.getString("status"));
        }

        if (!found) {
            System.out.println("|                         No reservations found.                           |");
        }

        System.out.println("+------+----------------+--------------+--------------+--------+-----------+");
    }
}
