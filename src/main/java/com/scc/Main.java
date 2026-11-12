
package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("\n================================");
        System.out.println("    RESTAURANT RESERVATION");
        System.out.println("================================");

        // Create database tables first.
        DatabaseInitializer.initialize();

        // Start authentication.
        try (Scanner sc = new Scanner(System.in)) {
            Authentication.start(sc);
        }
    }
}
