
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

                    System.out.println("Logging out from Manager Dashboard...");
                    running = false;
                }

                case 8 -> viewAuditLogs();

                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // VIEW ALL USERS
    private void viewAllUsers() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                ALL REGISTERED USERS              |");
        System.out.println("+--------------------------------------------------+");

        displayUsers(userDao.getAllUser());
    }

    // VIEW PENDING USERS
    private void viewPendingUsers() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                   PENDING USERS                  |");
        System.out.println("+--------------------------------------------------+");

        displayUsers(userDao.getPendingUser());
    }

    // DISPLAY USERS
    private void displayUsers(List<User> users) {

        if (users == null || users.isEmpty()) {
            System.out.println("| No registered users found.                       |");
            System.out.println("+--------------------------------------------------+");
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
            System.out.println("Unable to approve user. Check the ID or status.");
        }
    }

    // ADD NEW USER
    private void addNewUser() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                    ADD NEW USER                  |");
        System.out.println("+--------------------------------------------------+");
        System.out.println("| Public registration is for customers only.      |");
        System.out.println("| Use the registration menu to create an account. |");
        System.out.println("+--------------------------------------------------+");
    }

    // UPDATE USER ROLE / STATUS
    private void updateUser() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                    UPDATE USER                   |");
        System.out.println("+--------------------------------------------------+");

        List<User> users = userDao.getAllUser();

        if (users == null || users.isEmpty()) {
            System.out.println("| No registered users found.                       |");
            System.out.println("+--------------------------------------------------+");
            return;
        }

        displayUsers(users);

        System.out.print("\nEnter User ID to update: ");
        int userId = readNumber();

        User selected = null;

        for (User user : users) {
            if (user.id() == userId) {
                selected = user;
                break;
            }
        }

        if (selected == null) {
            System.out.println("User ID not found.");
            return;
        }

        if (selected.id() == admin.id()) {
            System.out.println("You cannot change your own manager account here.");
            return;
        }

        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                  AVAILABLE ROLES                 |");
        System.out.println("+--------------------------------------------------+");
        System.out.println("| MANAGER                                          |");
        System.out.println("| REGISTRAR                                        |");
        System.out.println("| CUSTOMER                                         |");
        System.out.println("+--------------------------------------------------+");
        System.out.print("Enter new role: ");

        String role = scanner.nextLine().trim().toUpperCase();

        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                 AVAILABLE STATUS                 |");
        System.out.println("+--------------------------------------------------+");
        System.out.println("| APPROVED                                         |");
        System.out.println("| PENDING                                          |");
        System.out.println("+--------------------------------------------------+");
        System.out.print("Enter new status: ");

        String status = scanner.nextLine().trim().toUpperCase();

        if (userDao.updateUserStatus(userId, role, status)) {
            logDao.logActivity(
                    admin.id(),
                    admin.username(),
                    "USER_UPDATED: User ID " + userId
                            + " | Role: " + role
                            + " | Status: " + status
            );

            System.out.println("User updated successfully.");
        } else {
            System.out.println("Unable to update user. Check the role and status.");
        }
    }

    // DELETE ACCOUNT
    private void deleteUser() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|                  DELETE ACCOUNT                  |");
        System.out.println("+--------------------------------------------------+");

        List<User> users = userDao.getAllUser();

        if (users == null || users.isEmpty()) {
            System.out.println("No registered users found.");
            return;
        }

        displayUsers(users);

        System.out.print("\nEnter User ID to delete: ");
        int userId = readNumber();

        if (userId == admin.id()) {
            System.out.println("You cannot delete your own manager account.");
            return;
        }

        System.out.print("Are you sure? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            if (userDao.deleteUser(userId)) {
                logDao.logActivity(
                        admin.id(),
                        admin.username(),
                        "USER_DELETED: User ID " + userId
                );

                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Unable to delete user.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
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
