
package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() {

        String[] tables = {

                // FOOD MENU
                """
            CREATE TABLE IF NOT EXISTS foods (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                price REAL NOT NULL CHECK(price >= 0),
                available INTEGER NOT NULL DEFAULT 1
            )
            """,

                // CUSTOMER RESERVATIONS
                """
            CREATE TABLE IF NOT EXISTS reservations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                reservation_date TEXT NOT NULL,
                reservation_time TEXT NOT NULL,
                guests INTEGER NOT NULL CHECK(guests > 0),
                status TEXT NOT NULL DEFAULT 'PENDING',
                FOREIGN KEY(user_id) REFERENCES users(id)
            )
            """,

                // CUSTOMER ORDERS
                """
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                order_date TEXT DEFAULT CURRENT_TIMESTAMP,
                status TEXT NOT NULL DEFAULT 'PENDING',
                total REAL NOT NULL DEFAULT 0,
                FOREIGN KEY(user_id) REFERENCES users(id)
            )
            """,

                // ORDER ITEMS
                """
            CREATE TABLE IF NOT EXISTS order_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                food_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL CHECK(quantity > 0),
                price REAL NOT NULL,
                FOREIGN KEY(order_id) REFERENCES orders(id),
                FOREIGN KEY(food_id) REFERENCES foods(id)
            )
            """,

                // CUSTOMER PAYMENT RECEIPTS
                """
            CREATE TABLE IF NOT EXISTS receipts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                reservation_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                payment_method TEXT NOT NULL,
                amount REAL NOT NULL CHECK(amount > 0),
                receipt_path TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'PENDING',
                receipt_date TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(reservation_id) REFERENCES reservations(id),
                FOREIGN KEY(user_id) REFERENCES users(id)
            )
            """
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : tables) {
                stmt.execute(sql);
            }

            System.out.println(
                    "-> Database tables initialized successfully!"
            );

        } catch (SQLException e) {
            System.err.println(
                    "Database initialization error: " + e.getMessage()
            );
        }
    }
}
