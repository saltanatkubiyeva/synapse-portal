package kz.synapse.services;

import kz.synapse.database.Database;
import kz.synapse.models.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class CourseStatistics {

    private final CourseOffering offering;

    public CourseStatistics(CourseOffering offering) {
        this.offering = offering;
    }

    public double getPassRate() {
        int total = offering.getEnrolledStudents().size();
        if (total == 0) return 0.0;
        long passed = offering.getEnrolledStudents().stream()
                .filter(s -> {
                    Mark m = offering.getMark(s);
                    return m != null && m.isPassed();
                }).count();
        return ((double) passed / total) * 100.0;
    }

    public double getFailRate() { return 100.0 - getPassRate(); }

    public double getAvgGpa() {
        return offering.getEnrolledStudents().stream()
                .filter(s -> offering.getMark(s) != null)
                .mapToDouble(s -> offering.getMark(s).getGpaEquivalent())
                .average().orElse(0.0);
    }

    public Map<String, Long> getGradeDistribution() {
        return offering.getEnrolledStudents().stream()
                .filter(s -> offering.getMark(s) != null)
                .map(s -> offering.getMark(s).getLetterGrade())
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()));
    }

    public List<Student> getTopStudents(int limit) {
        return offering.getEnrolledStudents().stream()
                .filter(s -> offering.getMark(s) != null)
                .sorted((a, b) -> Double.compare(
                        offering.getMark(b).getTotal(),
                        offering.getMark(a).getTotal()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public String generateReport() {
        return String.format(
                "--- COURSE REPORT: %s [%s] ---\n" +
                        "Total Enrolled: %d\nPass Rate: %.1f%%\n" +
                        "Fail Rate: %.1f%%\nAverage GPA: %.2f\n" +
                        "Grade Distribution: %s",
                offering.getCourse().getName(), offering.getSemester(),
                offering.getEnrolledStudents().size(),
                getPassRate(), getFailRate(), getAvgGpa(),
                getGradeDistribution());
    }
}
