package org.example;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class ExpenseTracker {

    private String userName;
    private double weeklyBudget;
    private int startDay; // 0 = Sunday, 1 = Monday, etc.
    private String[] rawData = new String[82]; // Day 1 to Day 82

    private static final String[] WEEKDAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static final DecimalFormat peso = new DecimalFormat("₱#,##0.00");

    public void configureNewTracker(Scanner scanner) {
        System.out.print("Enter your weekly budget (e.g. 10000): ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a numeric value for the budget.");
            System.out.print("Enter your weekly budget (e.g. 10000): ");
            scanner.next(); // consume invalid input
        }
        double budget = scanner.nextDouble();
        setBudget(budget);
        scanner.nextLine(); // Consume newline

        System.out.print("Enter the start day of the week (0 for Sunday, 1 for Monday, ... 6 for Saturday): ");
        int startDayInput;
        while (true) {
            if (scanner.hasNextInt()) {
                startDayInput = scanner.nextInt();
                if (startDayInput >= 0 && startDayInput <= 6) {
                    break; // Valid input
                } else {
                    System.out.println("Invalid day. Please enter a number between 0 and 6.");
                    System.out.print("Enter the start day of the week (0-6): ");
                }
            } else {
                System.out.println("Invalid input. Please enter a number for the start day.");
                System.out.print("Enter the start day of the week (0-6): ");
                scanner.next(); // consume invalid input
            }
        }
        setWeekStartDay(startDayInput);
        scanner.nextLine(); // Consume newline
    }

    public void loadData(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("name=")) {
                    userName = line.substring(line.indexOf('=') + 1).trim();
                } else if (line.startsWith("day-")) {
                    if (index < 82) {
                        rawData[index++] = line.trim();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading data file: " + e.getMessage());
        }
    }

    public void setBudget(double budget) {
        this.weeklyBudget = budget;
    }

    public void setWeekStartDay(int day) {
        this.startDay = day;
    }

    public void displayWeeklySummary(int weekNumber) {
        int startIndex = (weekNumber - 1) * 7;
        int endIndex = Math.min(startIndex + 7, rawData.length);

        if (startIndex >= rawData.length) {
            System.out.println("Week number is too high. No data available.");
            return;
        }

        double totalSpent = 0;
        List<String> categories = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        // Display heading
        System.out.println("\n=== WEEK " + weekNumber + ": Days " + (startIndex + 1) + " to " + (endIndex) + " ===");
        System.out.println("Allocated Budget: " + peso.format(weeklyBudget));
        System.out.println("\n----------------------------");

        // Process each day's expenses
        for (int i = startIndex; i < endIndex; i++) {
            if (rawData[i] == null || !rawData[i].contains("=")) continue;

            String expensePart = rawData[i].substring(rawData[i].indexOf('=') + 1);
            String[] items = expensePart.split("&");

            for (String item : items) {
                String[] parts = item.split("_");
                if (parts.length == 2) {
                    String category = capitalize(parts[0]);
                    double amount = Double.parseDouble(parts[1]);

                    totalSpent += amount;

                    int catIndex = categories.indexOf(category);
                    if (catIndex != -1) {
                        amounts.set(catIndex, amounts.get(catIndex) + amount);
                    } else {
                        categories.add(category);
                        amounts.add(amount);
                    }
                }
            }
        }

        // Display category breakdown
        System.out.println("Category\tAmount");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%-15s %s\n", categories.get(i) + ":", peso.format(amounts.get(i)));
        }

        // Final summary
        System.out.println("\n----------------------------");
        System.out.println("Amount Remaining: " + peso.format(weeklyBudget - totalSpent));
        System.out.println("----------------------------\n");
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return "Uncategorized";
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}