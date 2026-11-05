package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner sc){
        this.userDao = userDao;
        this.sc = sc;
    }

        public void start(){
            System.out.println("\n===========================");
            System.out.println(" RESTAURANT RESERVATION");
            System.out.println("===========================");
            System.out.println("|1. Login                 |");
            System.out.println("|2. Register              |");
            System.out.println("|3. Exit                  |");
            System.out.println("===========================");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice){
                case "1":
                    handlelogin();
                    break;
                case "2":
                    System.out.print("Enter new Username: ");
                    String user = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();

                    System.out.print("Select Role [1] MANAGER(ADMIN) [2] CUSTOMER: ");
                    int rolechoice = Integer.parseInt(sc.nextLine());
                    String role;
                    switch (rolechoice){
                        case 1:
                            role = "ADMIN";
                            break;
                        case 2:
                            role = "CUSTOMER";
                            break;
                        default:
                            role = null;
                    }
                    if(role != null){
                        userDao.registerUser(user, pass, role);

                    }else {
                        System.out.println("Invalid role Selection");
                    }
            }
        }

    private void handlelogin(){
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = userDao.login(username, password);

        if(user != null){
            System.out.println("\n-> Welcome "+user.username()+" | ROLE: "+user.roles()+"");
            switch (user.roles()){
                case "ADMIN" -> new Manager(userDao, sc).display(user);
                default -> System.out.println("Account roles not found!");
            }
        }
    }
}
