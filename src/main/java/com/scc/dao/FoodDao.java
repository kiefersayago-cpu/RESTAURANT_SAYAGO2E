
package com.scc.dao;

import java.sql.*;

public class FoodDao {

    public void viewFoods() {

        String sql = "SELECT * FROM foods WHERE available = 1 ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n========== AVAILABLE FOOD MENU ==========");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.printf(
                        "%d. %s - PHP %.2f%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );

                System.out.println(
                        "Description: " + rs.getString("description")
                );
            }

            if (!found) {
                System.out.println("No available food items.");
            }

        } catch (SQLException e) {
            System.out.println("Food error: " + e.getMessage());
        }
    }

    // Registrar: add food
    public boolean addFood(
            String name, String description, double price) {

        String sql = """
            INSERT INTO foods(name, description, price)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, price);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add food error: " + e.getMessage());
            return false;
        }
    }

    // Registrar: delete food
    public boolean deleteFood(int id) {

        String sql = """
            UPDATE foods SET available = 0
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Delete food error: " + e.getMessage());
            return false;
        }
    }
}
