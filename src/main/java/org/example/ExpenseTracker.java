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

    // Renamed from configureNewTracker for more general use
    public void configureTrackerSession(Scanner scanner) {
        System.out.print("Enter/Confirm your weekly budget for this session (e.g. 10000): ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a numeric value for the budget.");
            System.out.print("Enter/Confirm your weekly budget (e.g. 10000): ");
            scanner.next(); // consume invalid input
        }
        double budget = scanner.nextDouble();
        setBudget(budget);
        scanner.nextLine(); // Consume newline

        System.out.print("Enter/Confirm the start day of the week for this session (0 for Sunday, 1 for Monday, ... 6 for Saturday): ");
        int startDayInput;
        while (true) {
            if (scanner.hasNextInt()) {
                startDayInput = scanner.nextInt();
                if (startDayInput >= 0 && startDayInput <= 6) {
                    break; // Valid input
                } else {
                    System.out.println("Invalid day. Please enter a number between 0 and 6.");
                    System.out.print("Enter/Confirm the start day of the week (0-6): ");
                }
            } else {
                System.out.println("Invalid input. Please enter a number for the start day.");
                System.out.print("Enter/Confirm the start day of the week (0-6): ");
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
            boolean nameFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("name=")) {
                    userName = line.substring(line.indexOf('=') + 1).trim();
                    nameFound = true;
                } else if (line.startsWith("day-")) {
                    if (index < rawData.length) {
                        rawData[index++] = line.trim();
                    } else {
                        System.out.println("Warning: More than " + rawData.length + " days of data in file. Some data may not be loaded.");
                        break;
                    }
                }
            }
            if (nameFound) {
                System.out.println("User '" + userName + "' data loaded.");
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

    /**
     * Prompts the user for a week number and displays the summary for that week.
     * @param scanner The Scanner object to read user input.
     */
    public void promptAndDisplayWeeklySummary(Scanner scanner) {
        if (this.weeklyBudget == 0 && (rawData == null || Arrays.stream(rawData).allMatch(Objects::isNull))) {
            System.out.println("\nTracker is not fully configured (budget might be zero and no data loaded).");
            System.out.println("Please ensure budget is set. Summary might be empty or reflect zero budget.");
        }
        System.out.print("\nEnter the week number to display summary (e.g., 1 to 12): ");
        int weekNum = 1; // Default
        if (scanner.hasNextInt()) {
            weekNum = scanner.nextInt();
            if (weekNum <= 0) { // Basic validation for week number
                System.out.println("Invalid week number. Week number must be positive. Displaying for week 1.");
                weekNum = 1;
            }
        } else {
            System.out.println("Invalid input for week number, defaulting to week 1.");
        }
        scanner.nextLine(); // Consume newline
        displayWeeklySummary(weekNum); // Calls the existing method
    }

    /**
     * Placeholder method for adding new expenses.
     * @param scanner The Scanner object to read user input.
     */
    public void addNewExpense(Scanner scanner) {
        System.out.println("\n--- Add New Expense ---");
        System.out.println("Functionality to add new expenses is not yet implemented.");
        System.out.println("This feature will allow you to record new spending entries.");
        // Future implementation will involve:
        // 1. Asking for expense amount, category, and day/date.
        // 2. Updating the rawData array or a more suitable data structure.
        // 3. Handling data persistence (saving).
    }

    public void displayWeeklySummary(int weekNumber) {
        // ... (existing displayWeeklySummary method remains unchanged) ...
        // (Ensure it handles weeklyBudget possibly being 0 if not set,
        // or rawData being empty gracefully, which it already does)

        int startIndex = (weekNumber - 1) * 7;
        int endIndex = Math.min(startIndex + 7, rawData.length);

        // Check if weekNumber is sensible given rawData length
        boolean hasDataForWeek = false;
        for(int k=startIndex; k < endIndex; k++) {
            if(k < rawData.length && rawData[k] != null) {
                hasDataForWeek = true;
                break;
            }
        }

        if (startIndex >= rawData.length && !hasDataForWeek && weekNumber > (rawData.length / 7) + (rawData.length % 7 > 0 ? 1 : 0) && rawData.length > 0 ) {
            System.out.println("\nWeek number " + weekNumber + " is too high for the available data (" + rawData.length + " days recorded).");
            return;
            //checks if week number is too high
        }

        if (startIndex < 0) {
            System.out.println("\nInvalid week number.");
            return;
            // Should not happen with positive weekNumber
        }


        double totalSpent = 0;
        List<String> categories = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        // Display heading
        System.out.println("\n=== WEEK " + weekNumber + ": Days " + (startIndex + 1) + " to " + (Math.max(startIndex +1, endIndex)) + " ===");
        System.out.println("Allocated Budget: " + peso.format(weeklyBudget));
        System.out.println("\n----------------------------");

        if (!hasDataForWeek && startIndex < rawData.length) { // Check if there's actually any data for this week even if within bounds
            System.out.println("No expense data recorded for this week.");
        } else {
            // Process each day's expenses
            for (int i = startIndex; i < endIndex; i++) {
                if (i >= rawData.length || rawData[i] == null || !rawData[i].contains("=")) {
                    // Optionally print a message for days with no data within the week
                    // System.out.println("No data for Day " + (i + 1));
                    continue;
                }

                String expensePart = rawData[i].substring(rawData[i].indexOf('=') + 1);
                if (expensePart.isEmpty()) continue; // Skip if there's nothing after '='

                String[] items = expensePart.split("&");

                for (String item : items) {
                    String[] parts = item.split("_");
                    if (parts.length == 2) {
                        String category = capitalize(parts[0]);
                        try {
                            double amount = Double.parseDouble(parts[1]);
                            totalSpent += amount;

                            int catIndex = categories.indexOf(category);
                            if (catIndex != -1) {
                                amounts.set(catIndex, amounts.get(catIndex) + amount);
                            } else {
                                categories.add(category);
                                amounts.add(amount);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Warning: Invalid amount format for item '" + item + "' on Day " + (i+1) + ". Skipping this item.");
                        }
                    } else if (!item.isEmpty()){
                        System.out.println("Warning: Malformed expense item '" + item + "' on Day " + (i+1) + ". Skipping this item.");
                    }
                }
            }
        }


        // Display category breakdown
        if (!categories.isEmpty()) {
            System.out.println("Category\tAmount");
            for (int i = 0; i < categories.size(); i++) {
                System.out.printf("%-15s %s\n", categories.get(i) + ":", peso.format(amounts.get(i)));
            }
        } else if (hasDataForWeek) { // If there was data but no valid expenses parsed
            System.out.println("No valid expenses to categorize for this week.");
        }


        // Final summary
        System.out.println("\n----------------------------");
        System.out.println("Total Spent this Week: " + peso.format(totalSpent));
        System.out.println("Amount Remaining: " + peso.format(weeklyBudget - totalSpent));
        System.out.println("----------------------------\n");
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return "Uncategorized";
        // Ensure word is not just symbols or numbers before trying to capitalize
        if (!Character.isLetter(word.charAt(0))) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}