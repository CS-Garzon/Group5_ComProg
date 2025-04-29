package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your weekly budget (e.g. 10000): ");
        double budget = scanner.nextDouble();

        System.out.print("Enter the start day of the week (0 for Sunday, 1 for Monday, ... 6 for Saturday): ");
        int startDay = scanner.nextInt();

        System.out.print("Enter the week number (1 to 12): ");
        int weekNum = scanner.nextInt();

        ExpenseTracker tracker = new ExpenseTracker();
        tracker.loadData("User.txt");
        tracker.setBudget(budget);
        tracker.setWeekStartDay(startDay);
        tracker.displayWeeklySummary(weekNum);
        }
}