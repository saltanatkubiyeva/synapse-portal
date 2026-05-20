package kz.synapse.ui;

import kz.synapse.utils.LanguageManager;

import java.util.Scanner;

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

    /**
     * Показывает расписание всех дисциплин текущего семестра.
     * Доступно всем ролям кроме Admin и TechSupport.
     * Группирует по дисциплине, показывает все слоты.
     */
    public static void viewSemesterSchedule() {
        kz.synapse.database.Database db = kz.synapse.database.Database.getInstance();
        kz.synapse.utils.LanguageManager lm = kz.synapse.utils.LanguageManager.getInstance();
        clearScreen();
        printLine();
        String sem = db.getCurrentSemester() != null
                ? db.getCurrentSemester().toString()
                : lm.get("common.schedule.noSemester");
        System.out.println(lm.get("common.schedule.title", sem));
        printLine();

        java.util.List<kz.synapse.models.CourseOffering> offerings =
                db.getPublishedOfferings().stream()
                        .filter(o -> o.getSemester() == db.getCurrentSemester())
                        .collect(java.util.stream.Collectors.toList());

        if (offerings.isEmpty()) {
            System.out.println(lm.get("common.schedule.noPublished"));
            pressEnter();
            return;
        }

        for (kz.synapse.models.CourseOffering o : offerings) {
            System.out.printf("%n  %-8s %-30s | %d/%d seats | Head: %s%n",
                    o.getCourse().getCourseCode(),
                    o.getCourse().getName(),
                    o.getEnrolledStudents().size(),
                    o.getMaxStudents(),
                    o.getHeadLecturer() != null ? o.getHeadLecturer().getName() : "—");

            java.util.List<kz.synapse.models.LessonSlot> slots = o.getSlots();
            if (slots.isEmpty()) {
                System.out.println(lm.get("common.schedule.noSlots"));
            } else {
                slots.stream()
                        .sorted(java.util.Comparator
                                .comparing(kz.synapse.models.LessonSlot::getDayOfWeek)
                                .thenComparing(kz.synapse.models.LessonSlot::getStartTime))
                        .forEach(s -> System.out.printf(
                                "    [%-8s] %-9s %s–%s | Room: %-6s | %s | %d/%d%n",
                                s.getType(), s.getDayOfWeek(),
                                s.getStartTime(), s.getEndTime(),
                                s.getRoom(), s.getTeacher().getName(),
                                s.getCurrentStudents(), s.getMaxStudents()));
            }
        }
        printLine();
        pressEnter();
    }

}