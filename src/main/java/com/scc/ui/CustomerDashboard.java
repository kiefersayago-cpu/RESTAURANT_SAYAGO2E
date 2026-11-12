
package com.scc.ui;

import com.scc.dao.*;
import com.scc.model.User;

import java.util.Scanner;

public class CustomerDashboard {

    private static final FoodDao foodDao = new FoodDao();
    private static final ReservationDao reservationDao =
            new ReservationDao();
    private static final ReceiptDao receiptDao = new ReceiptDao();

    public static void start(Scanner sc, User user) {

        if (user == null
                || !"CUSTOMER".equalsIgnoreCase(user.roles())
                || !"APPROVED".equalsIgnoreCase(user.status())) {

            System.out.println("Access Denied!");
            return;
        }

        boolean running = true;

        while (running) {

            System.out.println("\n==================================");
            System.out.println("        CUSTOMER DASHBOARD");
            System.out.println("==================================");
            System.out.println("Welcome, " + user.username());
            System.out.println("----------------------------------");
            System.out.println("| 1. View Food Menu              |");
            System.out.println("| 2. Reserve a Table             |");
            System.out.println("| 3. Submit Payment Receipt      |");
            System.out.println("| 4. View My Reservations        |");
            System.out.println("| 5. Logout                      |");
            System.out.println("==================================");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    foodDao.viewFoods();
                    break;

                case "2":
                    reserveTable(sc, user);
                    break;

                case "3":
                    submitReceipt(sc, user);
                    break;

                case "4":
                    reservationDao.viewMyReservations(user.id());
                    break;

                case "5":
                    running = false;
                    System.out.println("Customer logged out.");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void reserveTable(Scanner sc, User user) {

        System.out.println("\n========== RESERVE A TABLE ==========");

        System.out.print("Reservation Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        System.out.print("Reservation Time (HH:MM): ");
        String time = sc.nextLine().trim();

        try {
            System.out.print("Number of Guests: ");
            int guests = Integer.parseInt(sc.nextLine());

            if (guests <= 0) {
                System.out.println("Invalid number of guests.");
                return;
            }

            if (date.isBlank() || time.isBlank()) {
                System.out.println("Date and time are required.");
                return;
            }

            if (reservationDao.createReservation(
                    user.id(), date, time, guests)) {

                System.out.println("Reservation submitted!");
                System.out.println("Status: PENDING");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid guest number.");
        }
    }

    private static void submitReceipt(Scanner sc, User user) {

        System.out.println("\n========== SUBMIT RECEIPT ==========");

        reservationDao.viewMyReservations(user.id());

        try {
            System.out.print("Reservation ID: ");
            int reservationId = Integer.parseInt(sc.nextLine());

            if (!reservationDao.ownsReservation(
                    reservationId, user.id())) {

                System.out.println("Reservation not found.");
                return;
            }

            System.out.print("Payment Method: ");
            String method = sc.nextLine();

            System.out.print("Amount Paid: PHP ");
            double amount = Double.parseDouble(sc.nextLine());

            if (amount <= 0 || method.isBlank()) {
                System.out.println("Invalid payment details.");
                return;
            }

            System.out.print("Receipt File Path: ");
            String path = sc.nextLine().trim();

            if (path.isBlank()) {
                System.out.println("Receipt path is required.");
                return;
            }

            if (receiptDao.addReceipt(
                    reservationId,
                    user.id(),
                    method,
                    amount,
                    path)) {

                System.out.println("Receipt submitted successfully!");
                System.out.println("Status: PENDING VERIFICATION");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID or amount.");
        }
    }
}
