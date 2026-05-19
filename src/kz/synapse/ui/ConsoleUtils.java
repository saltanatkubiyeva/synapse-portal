package kz.synapse.ui;

import kz.synapse.utils.LanguageManager;

import java.util.Scanner;

/** утилиты консольного ввода/вывода. */
public class ConsoleUtils {

    private static final Scanner scanner = new Scanner(System.in);

    // ввод

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println(UIStrings.get("msg.invalid"));
            }
        }
    }

    // вывод

    public static void pressEnter() {
        System.out.print(UIStrings.get("prompt.enter"));
        scanner.nextLine();
    }

    public static void printLine() {
        System.out.println(LanguageManager.get("ui.separator"));
    }

    public static void printHeader(String key) {
        System.out.println();
        printLine();
        System.out.println("  " + UIStrings.get(key));
        printLine();
    }

    public static void success(String msg) {
        System.out.println(LanguageManager.get("msg.success.prefix") + msg);
    }

    public static void error(String msg) {
        System.out.println(LanguageManager.get("msg.error.prefix") + LanguageManager.get("msg.error") + msg);
    }

    public static void info(String msg) {
        System.out.println("  " + msg);
    }

    /** очистка экрана (работает в большинстве терминалов). */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
