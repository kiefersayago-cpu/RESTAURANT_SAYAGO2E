
package com.scc.dao;

import java.sql.*;

public class ReceiptDao {

    // CUSTOMER: submit receipt
    public boolean addReceipt(
            int reservationId,
            int userId,
            String method,
            double amount,
            String path) {

        String sql = """
            INSERT INTO receipts
            (reservation_id, user_id, payment_method,
             amount, receipt_path, status)
            VALUES (?, ?, ?, ?, ?, 'PENDING')
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ps.setInt(2, userId);
            ps.setString(3, method);
            ps.setDouble(4, amount);
            ps.setString(5, path);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Receipt error: " + e.getMessage());
            return false;
        }
    }

    // REGISTRAR: view receipts
    public void viewReceipts() {

        String sql = """
            SELECT p.*, u.username
            FROM receipts p
            JOIN users u ON p.user_id = u.id
            ORDER BY p.id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n========== PAYMENT RECEIPTS ==========");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("-----------------------------");
                System.out.println("Receipt ID: " + rs.getInt("id"));
                System.out.println("Customer: " + rs.getString("username"));
                System.out.println("Reservation ID: " + rs.getInt("reservation_id"));
                System.out.println("Method: " + rs.getString("payment_method"));
                System.out.println("Amount: PHP " + rs.getDouble("amount"));
                System.out.println("File: " + rs.getString("receipt_path"));
                System.out.println("Status: " + rs.getString("status"));
            }

            if (!found) {
                System.out.println("No receipts submitted.");
            }

        } catch (SQLException e) {
            System.out.println("Receipt viewing error: " + e.getMessage());
        }
    }

    // REGISTRAR: approve or reject receipt
    public boolean updateReceiptStatus(int receiptId, String status) {

        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            System.out.println("Invalid receipt status.");
            return false;
        }

        String sql = """
            UPDATE receipts SET status = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, receiptId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
            return false;
        }
    }
}
