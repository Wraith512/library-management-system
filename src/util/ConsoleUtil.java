package util;

import java.util.Scanner;

/**
 * Console I/O utility methods.
 * Demonstrates: Utility class, method overloading.
 */
public class ConsoleUtil {

    private static final Scanner scanner = new Scanner(System.in);

    // ANSI colour codes
    public static final String RESET  = "\u001B[0m";
    public static final String GREEN  = "\u001B[32m";
    public static final String RED    = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN   = "\u001B[36m";
    public static final String BOLD   = "\u001B[1m";

    private ConsoleUtil() {}   // utility class — no instances

    // ── Print helpers (method overloading) ───────────────────────────────────
    public static void success(String msg) { System.out.println(GREEN  + "✔ " + msg + RESET); }
    public static void error  (String msg) { System.out.println(RED    + "✘ " + msg + RESET); }
    public static void warn   (String msg) { System.out.println(YELLOW + "⚠ " + msg + RESET); }
    public static void info   (String msg) { System.out.println(CYAN   + "ℹ " + msg + RESET); }
    public static void header (String msg) {
        System.out.println();
        System.out.println(BOLD + CYAN + "═".repeat(70));
        System.out.println("  " + msg.toUpperCase());
        System.out.println("═".repeat(70) + RESET);
    }
    public static void divider()           { System.out.println("─".repeat(70)); }

    // ── Input helpers ────────────────────────────────────────────────────────
    public static String prompt(String label) {
        System.out.print(YELLOW + label + ": " + RESET);
        return scanner.nextLine().trim();
    }

    /** Overloaded - with a default value shown in brackets */
    public static String prompt(String label, String defaultVal) {
        System.out.print(YELLOW + label + " [" + defaultVal + "]: " + RESET);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultVal : input;
    }

    public static int promptInt(String label) {
        while (true) {
            try {
                String input = prompt(label);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                error("Please enter a valid integer.");
            }
        }
    }

    /** Overloaded - with range validation */
    public static int promptInt(String label, int min, int max) {
        while (true) {
            int val = promptInt(label);
            if (val >= min && val <= max) return val;
            error("Value must be between " + min + " and " + max + ".");
        }
    }

    public static double promptDouble(String label) {
        while (true) {
            try {
                return Double.parseDouble(prompt(label));
            } catch (NumberFormatException e) {
                error("Please enter a valid number.");
            }
        }
    }

    public static boolean promptYesNo(String label) {
        while (true) {
            String input = prompt(label + " (y/n)").toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no"))  return false;
            error("Please enter 'y' or 'n'.");
        }
    }

    public static void pause() {
        System.out.print(CYAN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
    }
}
