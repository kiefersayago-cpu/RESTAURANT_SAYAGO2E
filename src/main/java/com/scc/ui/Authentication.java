package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Authentication {

    private static final Logger log = LoggerFactory.getLogger(Authentication.class);
    private final UserDao userDao;
    private ActivityLogDao logDao = new ActivityLogDao();
    private final Scanner sc;

    public Authentication(UserDao userDao, ActivityLogDao logDao, Scanner sc){
        this.userDao = userDao;
        this.logDao = this.logDao;
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

                    System.out.print("Select Role [1] MANAGER(ADMIN) [2] CUSTOMER [3]REGISTRAR: ");
                    int rolechoice = Integer.parseInt(sc.nextLine());
                    String role;
                    switch (rolechoice){
                        case 1:
                            role = "ADMIN";
                            break;
                        case 2:
                            role = "CUSTOMER";
                            break;
                        case 3:
                            role = "REGISTRAR";
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
            logDao.logActivity(user.id(), user.username(), "USER_LOGIN");
            System.out.println("\n-> Welcome "+user.username()+" | ROLE: "+user.roles()+"");
            switch (user.roles()){
                case "ADMIN" -> new Manager(userDao,logDao, sc).display(user);
                default -> System.out.println("Account roles not found!");
            }
        }
    }
}
