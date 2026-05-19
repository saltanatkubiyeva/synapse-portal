package kz.synapse.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    private Course course;

    // set студентов которые пришли
    private Map<LocalDate, Set<Student>> records = new HashMap<>();

    public Attendance(Course course) {
        this.course = course;
    }

    // отметкааа

    public void markPresent(LocalDate date, Student student) {
        records.putIfAbsent(date, new HashSet<>());
        records.get(date).add(student);
    }

    public void markAbsent(LocalDate date, Student student) {
        if (records.containsKey(date)) {
            records.get(date).remove(student);
        }
    }

    // отметить всех сразу
    public void markAllPresent(LocalDate date, Set<Student> students) {
        records.put(date, new HashSet<>(students));
    }

    // stats
    // посещаемость конкретного студента в процентах
    public double getAttendanceRate(Student student) {
        if (records.isEmpty()) return 0.0;

        long attended = records.values().stream()
                .filter(present -> present.contains(student))
                .count();

        return ((double) attended / records.size()) * 100.0;
    }

    // атт на конкретной дате
    public boolean wasPresent(Student student, LocalDate date) {
        return records.containsKey(date)
                && records.get(date).contains(student);
    }

    // кол-во пропусков
    public int getMissedClasses(Student student) {
        if (records.isEmpty()) return 0;

        long attended = records.values().stream()
                .filter(present -> present.contains(student))
                .count();

        return (int) (records.size() - attended);
    }

    // даты отсутствия
    public List<LocalDate> getAbsentDates(Student student) {
        return records.entrySet().stream()
                .filter(e -> !e.getValue().contains(student))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    // студенты присутствовавшие на конкретной дате
    public Set<Student> getPresentStudents(LocalDate date) {
        return records.getOrDefault(date, new HashSet<>());
    }

    // все даты занятий
    public Set<LocalDate> getAllDates() {
        return records.keySet();
    }

    // общее кол-во занятий
    public int getTotalClasses() {
        return records.size();
    }

    public Course getCourse() { return course; }

    @Override
    public String toString() {
        return String.format(
                "Attendance{course='%s', totalClasses=%d}",
                course.getName(), records.size()
        );
    }
}
