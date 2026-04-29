package kz.synapse.services;

import kz.synapse.models.Student;
import java.util.List;
import java.util.Map;

public class CourseStatistics {
    private double avgGpa;
    private double passRate;
    private double failRate;
    private List<Student> topStudents;

    public CourseStatistics() {
        this.avgGpa = 0.0;
        this.passRate = 0.0;
        this.failRate = 0.0;
    }

    public String generateReport() {
        return "Course Statistics Report:\n" +
                "Average GPA: " + avgGpa + "\n" +
                "Pass Rate: " + passRate + "%\n" +
                "Fail Rate: " + failRate + "%";
    }

    public Map<String, Integer> getGradeDistribution() {
        return null;
    }

    public double getAvgGpa() {
        return avgGpa;
    }

    public void setAvgGpa(double avgGpa) {
        this.avgGpa = avgGpa;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public double getFailRate() {
        return failRate;
    }

    public void setFailRate(double failRate) {
        this.failRate = failRate;
    }

    public List<Student> getTopStudents() {
        return topStudents;
    }

    public void setTopStudents(List<Student> topStudents) {
        this.topStudents = topStudents;
    }
}
