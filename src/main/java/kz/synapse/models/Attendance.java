package kz.synapse.models;

import java.util.HashMap;
import java.util.Map;

public class Attendance {
    private Map<Student, Boolean> records;

    public Attendance() {
        this.records = new HashMap<>();
    }

    public void markPresent(Student student) {
        records.put(student, true);
    }

    public void markAbsent(Student student) {
        records.put(student, false);
    }

    public double getAttendanceRate() {
        if (records.isEmpty()) return 0.0;
        long presentCount = records.values().stream().filter(p -> p).count();
        return (double) presentCount / records.size();
    }

    public Map<Student, Boolean> getRecords() {
        return records;
    }

    public void setRecords(Map<Student, Boolean> records) {
        this.records = records;
    }
}
