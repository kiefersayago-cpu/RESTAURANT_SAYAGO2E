
package com.scc.ui;

import com.scc.dao.FoodDao;
import com.scc.dao.ReceiptDao;
import com.scc.dao.ReservationDao;
import com.scc.model.User;

import java.util.Scanner;

public class RegistrarDashboard {

    private static final ReservationDao reservationDao =
            new ReservationDao();

    private static final FoodDao foodDao =
            new FoodDao();

    private static final ReceiptDao receiptDao =
            new ReceiptDao();

    public static void start(Scanner sc, User user) {

        if (!"REGISTRAR".equalsIgnoreCase(user.roles())
                || !"APPROVED".equalsIgnoreCase(user.status())) {
            System.out.println("Access denied.");
            return;
        }

        boolean running = true;

        while (running) {

            System.out.println("\n+--------------------------------------+");
            System.out.println("|           REGISTRAR DASHBOARD        |");
            System.out.println("+--------------------------------------+");
            System.out.println("| Welcome, " +
                    String.format("%-26s", user.username()) + "|");
            System.out.println("+--------------------------------------+");
            System.out.println("| 1. View Customer Reservations        |");
            System.out.println("| 2. Approve / Reject Reservations     |");
            System.out.println("| 3. Manage Food Menu                  |");
            System.out.println("| 4. View Payment Receipts             |");
            System.out.println("| 5. Verify Payment Receipt            |");
            System.out.println("| 6. Logout                            |");
            System.out.println("+--------------------------------------+");

            System.out.print("Choose an option: ");
            int choice = readNumber(sc);

            switch (choice) {

                case 1 -> reservationDao.viewAllReservations();

                case 2 -> manageReservations(sc);

                case 3 -> manageFoodMenu(sc);

                case 4 -> receiptDao.viewReceipts();

                case 5 -> verifyReceipt(sc);

                case 6 -> {
                    System.out.println("Logging out from Registrar Dashboard...");
                    running = false;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // RESERVATION MANAGEMENT
    private static void manageReservations(Scanner sc) {

        boolean managing = true;

        while (managing) {

            System.out.println("\n+--------------------------------------+");
            System.out.println("|          RESERVATION MANAGEMENT      |");
            System.out.println("+--------------------------------------+");
            System.out.println("| 1. View Pending Reservations         |");
            System.out.println("| 2. Approve Reservation               |");
            System.out.println("| 3. Reject Reservation                |");
            System.out.println("| 4. Back                              |");
            System.out.println("+--------------------------------------+");

            System.out.print("Choose an option: ");
            int choice = readNumber(sc);

            switch (choice) {

                case 1 -> reservationDao.viewPendingReservations();

                case 2 -> updateReservation(sc, "APPROVED");

                case 3 -> updateReservation(sc, "REJECTED");

                case 4 -> managing = false;

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // UPDATE RESERVATION
    private static void updateReservation(
            Scanner sc,
            String status) {

        reservationDao.viewPendingReservations();

        System.out.print("\nEnter Reservation ID: ");
        int reservationId = readNumber(sc);

        boolean updated = reservationDao.updateReservationStatus(
                reservationId,
                status
        );

        if (updated) {
            System.out.println("\nReservation successfully " + status + "!");
        } else {
            System.out.println("\nUnable to update reservation.");
            System.out.println("Check the ID or reservation status.");
        }
    }

    // FOOD MANAGEMENT
    private static void manageFoodMenu(Scanner sc) {

        boolean managing = true;

        while (managing) {

            System.out.println("\n+--------------------------------------+");
            System.out.println("|              FOOD MANAGEMENT         |");
            System.out.println("+--------------------------------------+");
            System.out.println("| 1. View Food Menu                    |");
            System.out.println("| 2. Add Food                          |");
            System.out.println("| 3. Remove Food                       |");
            System.out.println("| 4. Back                              |");
            System.out.println("+--------------------------------------+");

            System.out.print("Choose: ");
            int choice = readNumber(sc);

            switch (choice) {

                case 1 -> foodDao.viewFoods();

                case 2 -> {

                    System.out.print("Enter food name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter food description: ");
                    String description = sc.nextLine().trim();

                    System.out.print("Enter food price: ");
                    double price = readDouble(sc);

                    if (name.isBlank() || price <= 0) {
                        System.out.println("Invalid food details.");
                    } else {
                        foodDao.addFood(name, description, price);
                    }
                }

                case 3 -> {

                    foodDao.viewFoods();

                    System.out.print("Enter Food ID to remove: ");
                    int foodId = readNumber(sc);

                    foodDao.deleteFood(foodId);
                }

                case 4 -> managing = false;

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // VERIFY PAYMENT RECEIPT
    private static void verifyReceipt(Scanner sc) {

        receiptDao.viewReceipts();

        System.out.println("\n+--------------------------------------+");
        System.out.println("|          VERIFY PAYMENT RECEIPT      |");
        System.out.println("+--------------------------------------+");
        System.out.println("| 1. Approve Receipt                   |");
        System.out.println("| 2. Reject Receipt                    |");
        System.out.println("| 3. Back                              |");
        System.out.println("+--------------------------------------+");

        System.out.print("Choose: ");
        int choice = readNumber(sc);

        String status;

        if (choice == 1) {
            status = "APPROVED";
        } else if (choice == 2) {
            status = "REJECTED";
        } else if (choice == 3) {
            return;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter Receipt ID: ");
        int receiptId = readNumber(sc);

        if (receiptDao.updateReceiptStatus(receiptId, status)) {
            System.out.println("Receipt successfully " + status + "!");
        } else {
            System.out.println("Unable to update receipt.");
        }
    }

    // READ INTEGER
    private static int readNumber(Scanner sc) {

        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.nextLine();
        }

        int number = sc.nextInt();
        sc.nextLine();

        return number;
    }

    // READ DECIMAL
    private static double readDouble(Scanner sc) {

        while (!sc.hasNextDouble()) {
            System.out.print("Enter a valid price: ");
            sc.nextLine();
        }

        double number = sc.nextDouble();
        sc.nextLine();

        return number;
    }
}
