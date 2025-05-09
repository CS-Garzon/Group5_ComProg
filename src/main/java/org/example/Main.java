package org.example;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseTracker tracker = new ExpenseTracker(); // Create tracker instance once

        System.out.println("Welcome to Your Expense Tracker!");
        System.out.println("---------------------------------");
        System.out.println("1. Start New Tracker");
        System.out.println("2. Load Existing Tracker");
        System.out.print("Choose an option (1 or 2): ");

        int choice = 0;
        // Basic input validation for choice
        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
        } else {
            System.out.println("Invalid input. Please run the program again and enter a number (1 or 2).");
            scanner.close();
            return;
        }
        scanner.nextLine(); // Consume the leftover newline

        File userFile = new File("filename.txt");

        if (choice == 1) { // Path 1: Start New Tracker
            System.out.println("\n--- Starting New Tracker ---");
            tracker.configureNewTracker(scanner);

        } else if (choice == 2) { // Path 2: Start Existing Tracker
            System.out.println("\n--- Loading Existing Tracker ---");
            if (!userFile.exists() || !userFile.isFile()) {
                System.out.println("Warning: Existing tracker data file ('" + userFile.getName() + "') not found or is not a file.");
                System.out.println("You can set up a new configuration, but no past expenses will be loaded.");
                // Proceed to set budget and start day as if it's a new setup without loading data
                tracker.configureNewTracker(scanner);
            }

            // For existing tracker (whether file was found or not), ask which week to display
            System.out.print("\nEnter the week number to display summary (1 to 12): ");
            int weekNum = 1; // Default
            if (scanner.hasNextInt()) {
                weekNum = scanner.nextInt();
            } else {
                System.out.println("Invalid input for week number, defaulting to week 1.");
            }
            scanner.nextLine(); // Consume newline
            tracker.displayWeeklySummary(weekNum);

        } else {
            System.out.println("Invalid choice. Exiting program.");
        }

        System.out.println("\nThank you for using the Expense Tracker!");
        scanner.close();
    }
}