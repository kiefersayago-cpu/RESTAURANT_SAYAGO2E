
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;
import com.scc.model.ActivityLog;

import java.util.List;
import java.util.Scanner;

public class Manager {

    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner scanner;

    private User admin;

    public Manager(UserDao userDao, ActivityLogDao logDao, Scanner scanner) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;
    }

    // MANAGER DASHBOARD
    public void display(User admin) {
        this.admin = admin;

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("+--------------------------------------------------+");
            System.out.println("|                  FLAVORA SYSTEM                  |");
            System.out.println("|                 MANAGER DASHBOARD                |");
            System.out.println("+--------------------------------------------------+");
            System.out.println("|  1. View All Users                               |");
            System.out.println("|  2. View Pending User Approvals                  |");
            System.out.println("|  3. Approve User Registration                    |");
            System.out.println("|  4. Add New User                                 |");
            System.out.println("|  5. Update User Role / Status                    |");
            System.out.println("|  6. Delete Account                               |");
            System.out.println("|  7. Logout                                       |");
            System.out.println("|  8. View System Audit Logs                       |");
            System.out.println("+--------------------------------------------------+");

            System.out.print("Enter choice: ");
            int choice = readNumber();

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUser();
                case 4 -> addNewUser();
                case 5 -> updateUser();
                case 6 -> deleteUser();

                case 7 -> {
                    logDao.logActivity(
                            admin.id(),
                            admin.username(),
                            "USER_LOGOUT"
                    );

                    System.out.println("\nLogging out from Manager Dashboard...");
                    running = false;
                }

                case 8 -> viewAuditLogs();

                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // VIEW ALL USERS
    private void viewAllUsers() {
        System.out.println("\n=============== ALL REGISTERED USERS ===============");

        List<User> users = userDao.getAllUser();

        if (users == null || users.isEmpty()) {
            System.out.println("No registered users found.");
            return;
        }

        System.out.println("+------+----------------+------------+------------+");
        System.out.printf("| %-4s | %-14s | %-10s | %-10s |%n",
                "ID", "Username", "Role", "Status");
        System.out.println("+------+----------------+------------+------------+");

        for (User user : users) {
            System.out.printf("| %-4d | %-14s | %-10s | %-10s |%n",
                    user.id(),
                    user.username(),
                    user.roles(),
                    user.status());
        }

        System.out.println("+------+----------------+------------+------------+");
    }

    // VIEW PENDING USERS
    private void viewPendingUsers() {
        System.out.println("\n=============== PENDING USERS ===============");

        List<User> users = userDao.getPendingUser();

        if (users == null || users.isEmpty()) {
            System.out.println("No pending users found.");
            return;
        }

        System.out.println("+------+----------------+------------+------------+");
        System.out.printf("| %-4s | %-14s | %-10s | %-10s |%n",
                "ID", "Username", "Role", "Status");
        System.out.println("+------+----------------+------------+------------+");

        for (User user : users) {
            System.out.printf("| %-4d | %-14s | %-10s | %-10s |%n",
                    user.id(),
                    user.username(),
                    user.roles(),
                    user.status());
        }

        System.out.println("+------+----------------+------------+------------+");
    }

    // APPROVE USER
    private void approveUser() {
        viewPendingUsers();

        System.out.print("\nEnter User ID to approve: ");
        int userId = readNumber();

        if (userDao.approveUser(userId)) {
            logDao.logActivity(
                    admin.id(),
                    admin.username(),
                    "USER_APPROVED: User ID " + userId
            );

            System.out.println("User approved successfully.");
        } else {
            System.out.println("Unable to approve user.");
        }
    }

    // ADD NEW USER
    private void addNewUser() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                    ADD NEW USER                  |");
        System.out.println("+--------------------------------------------------+");
        System.out.println("| This option is not yet connected to your        |");
        System.out.println("| registration method.                            |");
        System.out.println("+--------------------------------------------------+");
    }

    // UPDATE USER ROLE / STATUS
    private void updateUser() {
        System.out.println("\n=============== UPDATE USER ===============");

        List<User> users = userDao.getAllUser();

        if (users == null || users.isEmpty()) {
            System.out.println("No registered users found.");
            return;
        }

        System.out.println("+------+----------------+------------+------------+");
        System.out.printf("| %-4s | %-14s | %-10s | %-10s |%n",
                "ID", "Username", "Role", "Status");
        System.out.println("+------+----------------+------------+------------+");

        for (User user : users) {
            System.out.printf("| %-4d | %-14s | %-10s | %-10s |%n",
                    user.id(),
                    user.username(),
                    user.roles(),
                    user.status());
        }

        System.out.println("+------+----------------+------------+------------+");

        System.out.print("\nEnter User ID to update: ");
        int userId = readNumber();

        System.out.print("Enter new role (MANAGER[ADMIN]/REGISTRAR/CUSTOMER): ");
        String role = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter new status (APPROVED/PENDING): ");
        String status = scanner.nextLine().trim().toUpperCase();

        if (userDao.updateUserStatus(userId, role, status)) {
            logDao.logActivity(
                    admin.id(),
                    admin.username(),
                    "USER_UPDATED: User ID " + userId
                            + " | Role: " + role
                            + " | Status: " + status
            );

            System.out.println("\nUser updated successfully.");
        } else {
            System.out.println("\nUnable to update user.");
        }
    }

    // DELETE ACCOUNT
    private void deleteUser() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                  DELETE ACCOUNT                  |");
        System.out.println("+--------------------------------------------------+");
        System.out.println("| This option is not yet connected to your        |");
        System.out.println("| UserDao delete method.                          |");
        System.out.println("+--------------------------------------------------+");
    }

    // VIEW SYSTEM AUDIT LOGS WITH PAGINATION
    private void viewAuditLogs() {
        int page = 1;
        int pageSize = 5;
        boolean viewing = true;

        while (viewing) {
            int totalPages = logDao.getTotalPages(pageSize);

            if (totalPages == 0) {
                System.out.println("\nNo audit logs found.");
                return;
            }

            if (page > totalPages) {
                page = totalPages;
            }

            List<ActivityLog> logs = logDao.getLogsByPage(page, pageSize);

            System.out.println("\n+--------------------------------------------------+");
            System.out.println("|                 SYSTEM AUDIT LOGS                |");
            System.out.println("+--------------------------------------------------+");

            for (ActivityLog log : logs) {
                System.out.println(log);
            }

            System.out.println("+--------------------------------------------------+");
            System.out.println("| Page " + page + " of " + totalPages);
            System.out.println("| 1. Previous Page");
            System.out.println("| 2. Next Page");
            System.out.println("| 3. Back to Dashboard");
            System.out.println("+--------------------------------------------------+");

            System.out.print("Enter choice: ");
            int choice = readNumber();

            switch (choice) {
                case 1 -> {
                    if (page > 1) {
                        page--;
                    } else {
                        System.out.println("Already on the first page.");
                    }
                }

                case 2 -> {
                    if (page < totalPages) {
                        page++;
                    } else {
                        System.out.println("Already on the last page.");
                    }
                }

                case 3 -> viewing = false;

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // READ VALID NUMBER
    private int readNumber() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.nextLine();
        }

        int number = scanner.nextInt();
        scanner.nextLine();

        return number;
    }
}
