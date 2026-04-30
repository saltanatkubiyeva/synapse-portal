package kz.synapse.services;

import kz.synapse.models.Course;
import kz.synapse.models.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseStatistics {

    private Course course;

    public CourseStatistics(Course course) {
        this.course = course;
    }

    public double getPassRate() {
        List<Student> enrolled = new ArrayList<>(course.getEnrolledStudents());
        if (enrolled.isEmpty()) return 0.0;

        long passed = enrolled.stream()
                .filter(s -> s.getMarks().containsKey(course)
                        && s.getMarks().get(course).isPassed())
                .count();

        return ((double) passed / enrolled.size()) * 100.0;
    }

    public double getFailRate() {
        return 100.0 - getPassRate();
    }

    public double getAvgGpa() {
        List<Student> enrolled = new ArrayList<>(course.getEnrolledStudents());
        if (enrolled.isEmpty()) return 0.0;

        return enrolled.stream()
                .filter(s -> s.getMarks().containsKey(course))
                .mapToDouble(s -> s.getMarks()
                        .get(course).getGpaEquivalent())
                .average()
                .orElse(0.0);
    }

    public Map<String, Long> getGradeDistribution() {
        return course.getEnrolledStudents().stream()
                .filter(s -> s.getMarks().containsKey(course))
                .map(s -> s.getMarks().get(course).getLetterGrade())
                .collect(Collectors.groupingBy(
                        grade -> grade, Collectors.counting()
                ));
    }

    // топ студентов по оценке
    public List<Student> getTopStudents(int limit) {
        return new ArrayList<>(course.getEnrolledStudents())
                .stream()
                .filter(s -> s.getMarks().containsKey(course))
                .sorted((a, b) -> Double.compare(
                        b.getMarks().get(course).getTotal(),
                        a.getMarks().get(course).getTotal()
                ))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public String generateReport() {
        return String.format(
                "--- COURSE REPORT: %s ---\n" +
                        "Total Enrolled: %d\n" +
                        "Pass Rate: %.1f%%\n" +
                        "Fail Rate: %.1f%%\n" +
                        "Average GPA: %.2f\n" +
                        "Grade Distribution: %s",
                course.getName(),
                course.getEnrolledStudents().size(),
                getPassRate(),
                getFailRate(),
                getAvgGpa(),
                getGradeDistribution()
        );
    }
}