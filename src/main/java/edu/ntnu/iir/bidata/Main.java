package edu.ntnu.iir.bidata;

import edu.ntnu.iir.bidata.laddergame.Stigespillet;
import edu.ntnu.iir.bidata.clickgame.ClickGameApp;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose a game to start:");
        System.out.println("1. Ladder Game");
        System.out.println("2. Click Game");
        System.out.print("Enter 1 or 2: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("Starting Ladder Game...");
                Stigespillet.main(args);
                break;
            case "2":
                System.out.println("Starting Click Game...");
                ClickGameApp.main(args);
                break;
            default:
                System.out.println("Invalid choice. Exiting.");
        }
    }
}