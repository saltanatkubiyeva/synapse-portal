package kz.synapse.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Transcript implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Student student;
    private final Map<CourseOffering, Mark> records = new LinkedHashMap<>();

    public Transcript(Student student) {
        this.student = student;
    }

    public void addRecord(CourseOffering offering, Mark mark) {
        records.put(offering, mark);
    }


    public double calculateGpa() {
        if (records.isEmpty()) return 0.0;
        double totalPoints = 0;
        int totalCredits   = 0;
        for (Map.Entry<CourseOffering, Mark> e : records.entrySet()) {
            if (e.getValue().isPassed()) {
                int cr = e.getKey().getCourse().getCredits();
                totalPoints  += e.getValue().getGpaEquivalent() * cr;
                totalCredits += cr;
            }
        }
        return totalCredits == 0 ? 0 : totalPoints / totalCredits;
    }

    public int getTotalPassedCredits() {
        return records.entrySet().stream()
                .filter(e -> e.getValue().isPassed())
                .mapToInt(e -> e.getKey().getCourse().getCredits())
                .sum();
    }

    /** сданные курсы для пререквизитов */
    public List<Course> getPassedCourses() {
        return records.entrySet().stream()
                .filter(e -> e.getValue().isPassed())
                .map(e -> e.getKey().getCourse())
                .collect(Collectors.toList());
    }

    public List<Course> getFailedCourses() {
        return records.entrySet().stream()
                .filter(e -> !e.getValue().isPassed())
                .map(e -> e.getKey().getCourse())
                .collect(Collectors.toList());
    }

    public Map<CourseOffering, Mark> getRecords() {
        return Collections.unmodifiableMap(records);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Transcript: ").append(student.getName()).append(" ===\n");

        // группируем по семестру
        Map<String, List<Map.Entry<CourseOffering, Mark>>> bySemester =
                new LinkedHashMap<>();
        for (Map.Entry<CourseOffering, Mark> e : records.entrySet()) {
            String sem = e.getKey().getSemester().toString();
            bySemester.computeIfAbsent(sem, k -> new ArrayList<>()).add(e);
        }

        for (Map.Entry<String, List<Map.Entry<CourseOffering, Mark>>> semGroup
                : bySemester.entrySet()) {
            sb.append("\n[").append(semGroup.getKey()).append("]\n");
            sb.append(String.format("%-8s %-30s %5s %5s %5s %-4s %4s%n",
                    "Code", "Course", "ATT1", "ATT2", "FINAL", "GPA", "Grade"));
            sb.append("─".repeat(65)).append("\n");
            for (Map.Entry<CourseOffering, Mark> e : semGroup.getValue()) {
                Course c = e.getKey().getCourse();
                Mark m   = e.getValue();
                sb.append(String.format("%-8s %-30s %5.1f %5.1f %5.1f %-4.2f %4s%n",
                        c.getCourseCode(), c.getName(),
                        m.getAtt1(), m.getAtt2(), m.getFinalExam(),
                        m.getGpaEquivalent(), m.getLetterGrade()));
            }
        }

        sb.append("\n").append("─".repeat(65)).append("\n");
        sb.append(String.format("GPA: %.2f | Total Passed Credits: %d%n",
                calculateGpa(), getTotalPassedCredits()));
        return sb.toString();
    }
}
