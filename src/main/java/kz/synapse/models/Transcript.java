package kz.synapse.models;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Transcript implements Serializable {

    private Student student;
    private Map<Course, Mark> records = new LinkedHashMap<>();

    public Transcript(Student student) {
        this.student = student;
    }

    // добавить запись
    public void addRecord(Course course, Mark mark) {
        records.put(course, mark);
    }

    // гпа по транскрипту
    public double calculateGpa() {
        if (records.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;

        for (Map.Entry<Course, Mark> entry : records.entrySet()) {
                int cr = entry.getKey().getCredits();
                totalPoints += entry.getValue().getGpaEquivalent() * cr;
                totalCredits += cr;
        }
        return totalCredits == 0 ? 0 : totalPoints / totalCredits;
    }

    // общие кредиты сданных курсов
    public int getTotalPassedCredits() {
        return records.entrySet().stream()
                .filter(e -> e.getValue().isPassed())
                .mapToInt(e -> e.getKey().getCredits())
                .sum();
    }

    // список сданных курсов
    public List<Course> getPassedCourses() {
        return records.entrySet().stream()
                .filter(e -> e.getValue().isPassed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // список проваленных курсов
    public List<Course> getFailedCourses() {
        return records.entrySet().stream()
                .filter(e -> !e.getValue().isPassed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<Course, Mark> getRecords() { return records; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Transcript: ")
                .append(student.getName())
                .append(" ===\n");

        records.forEach((course, mark) ->
                sb.append(course.getName())
                        .append(" | ")
                        .append(mark.getLetterGrade())
                        .append(" | ")
                        .append(course.getCredits())
                        .append(" credits\n")
        );

        sb.append("─────────────────────\n");
        sb.append("GPA: ")
                .append(String.format("%.2f", calculateGpa()))
                .append("\n");
        sb.append("Passed Credits: ")
                .append(getTotalPassedCredits());

        return sb.toString();
    }
}