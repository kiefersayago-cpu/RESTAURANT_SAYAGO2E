
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private static final UserDao userDao = new UserDao();

    public static void start(Scanner sc) {

        boolean running = true;

        while (running) {
            System.out.println("\n================================");
            System.out.println("          FLAVORA SYSTEM");
            System.out.println("================================");
            System.out.println("| 1. Login                     |");
            System.out.println("| 2. Register                  |");
            System.out.println("| 3. Exit                      |");
            System.out.println("================================");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> login(sc);
                case "2" -> register(sc);
                case "3" -> {
                    running = false;
                    System.out.println("Thank you for visiting Flavora!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // LOGIN
    private static void login(Scanner sc) {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Please enter your username and password.");
            return;
        }

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if (!"APPROVED".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is pending approval or inactive.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username());

        switch (user.roles().toUpperCase()) {

            case "ADMIN" -> {
                System.out.println("Opening Manager Dashboard...");

                Manager manager = new Manager(
                        userDao,
                        new ActivityLogDao(),
                        sc
                );

                manager.display(user);
            }

            case "REGISTRAR" -> {
                System.out.println("Opening Registrar Dashboard...");
                RegistrarDashboard.start(sc, user);
            }

            case "CUSTOMER" -> {
                System.out.println("Opening Customer Dashboard...");
                CustomerDashboard.start(sc, user);
            }

            default -> System.out.println("Unauthorized role.");
        }
    }

    // CUSTOMER REGISTRATION
    private static void register(Scanner sc) {

        System.out.println("\n========== CUSTOMER REGISTRATION ==========");

        System.out.print("New Username: ");
        String username = sc.nextLine().trim();

        System.out.print("New Password: ");
        String password = sc.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password are required.");
            return;
        }

        userDao.registerUser(username, password, "CUSTOMER");
    }
}
