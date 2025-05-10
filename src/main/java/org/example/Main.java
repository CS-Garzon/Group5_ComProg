package org.example;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseTracker expenseTracker = new ExpenseTracker(); // Create tracker instance once
        DateTracker dateTracker = new DateTracker();

        System.out.println(nowDateTracker()); //fix this wtf how do you show the current date, also we need to use the date somewhere by the way
        System.out.println("Welcome to Your Expense Tracker!");
        System.out.println("---------------------------------");
        System.out.println("1. Start New Tracker");
        System.out.println("2. Load Existing Tracker");
        System.out.print("Choose an option (1 or 2): ");

        int initialChoice;

        if (scanner.hasNextInt()) {
            initialChoice = scanner.nextInt();
        } else {
            System.out.println("Invalid initial input. Please run the program again and enter a number (1 or 2).");
            scanner.close();
            return;
        }
        scanner.nextLine(); // Consume the leftover newline

        File userFile = new File("filename.txt");
        boolean trackerReady = false;

        switch (initialChoice){
            case 1:     // Path 1: Start New Tracker
            System.out.println("\n--- Starting New Tracker ---");
            expenseTracker.configureTrackerSession(scanner); // Configure budget and start day
            System.out.println("\nNew tracker configured for this session.");
            trackerReady = true;
            break;

            case 2:     // Path 2: Start Existing Tracker
            System.out.println("\n--- Loading Existing Tracker ---");

            if (!userFile.exists() || !userFile.isFile()) {
                System.out.println("Warning: Existing tracker data file ('" + userFile.getName() + "') not found or is not a file.");
                System.out.println("You can set up a configuration for this session, but no past expenses will be loaded.");

                expenseTracker.configureTrackerSession(scanner); // Configure budget and start day

                System.out.println("\nTracker configured for this session. No previous expense data was loaded as file was missing.");

            } else {
                expenseTracker.loadData(String.valueOf(userFile)); // Load username and rawData
                // loadData now prints its own success/user message
                expenseTracker.configureTrackerSession(scanner); // Configure/confirm budget and start day for session
                System.out.println("\nExisting tracker loaded and configured for this session.");
            }
            trackerReady = true;

            default: System.out.println("Invalid initial choice. Exiting program.");
        }

        if (trackerReady) {
            int actionChoice;
            do {
                System.out.println("\nWhat would you like to do?");
                System.out.println("1. View Weekly Summary");
                System.out.println("2. Add New Expense");
                System.out.println("3. Exit");
                System.out.print("Choose an action (1-3): ");

                if (scanner.hasNextInt()) {
                    actionChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (actionChoice) {
                        case 1:
                            expenseTracker.promptDisplayWeeklySummary(scanner);
                            break;
                        case 2:
                            expenseTracker.addNewExpense(scanner);
                            break;
                        case 3:
                            System.out.println("Exiting tracker application.");
                            break;
                        default:
                            System.out.println("Invalid action choice. Please try again.");
                            break;
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number (1-3).");
                    scanner.nextLine(); // Consume invalid input
                    actionChoice = 0; // Set to a value that doesn't exit the loop
                }
            } while (actionChoice != 3);
        }

        System.out.println("\nThank you for using the Expense Tracker!");
        scanner.close();
    }
}