package kz.synapse.models;

import kz.synapse.database.Database;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

/** сервисный класс запросов расписания. */
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<LessonSlot> getByOffering(CourseOffering offering) {
        return offering.getSlots();
    }

    public List<LessonSlot> getByTeacher(Teacher teacher) {
        return Database.getInstance().getCourseOfferings().stream()
                .flatMap(o -> o.getSlots().stream())
                .filter(s -> s.getTeacher().equals(teacher))
                .collect(Collectors.toList());
    }

    /** личное расписание студента — только выбранные слоты */
    public List<LessonSlot> getByStudent(Student student) {
        return student.getAllChosenSlots();
    }

    public List<LessonSlot> getByDay(DayOfWeek day) {
        return Database.getInstance().getCourseOfferings().stream()
                .flatMap(o -> o.getSlots().stream())
                .filter(s -> s.getDayOfWeek() == day)
                .collect(Collectors.toList());
    }

    /** конфликт аудитории: та же комната, тот же день, пересечение */
    public boolean hasRoomConflict(LessonSlot newSlot) {
        return Database.getInstance().getCourseOfferings().stream()
                .flatMap(o -> o.getSlots().stream())
                .filter(s -> s.getRoom().equals(newSlot.getRoom()))
                .anyMatch(s -> s.overlapsWith(newSlot));
    }

    /** конфликт учителя: тот же учитель, пересечение времени */
    public boolean hasTeacherConflict(LessonSlot newSlot) {
        return Database.getInstance().getCourseOfferings().stream()
                .flatMap(o -> o.getSlots().stream())
                .filter(s -> s.getTeacher().equals(newSlot.getTeacher()))
                .anyMatch(s -> s.overlapsWith(newSlot));
    }

    /** конфликт расписания студента: новый слот пересекается */
    public boolean hasStudentConflict(Student student, LessonSlot newSlot) {
        return student.getAllChosenSlots().stream()
                .anyMatch(chosen -> chosen.overlapsWith(newSlot));
    }
}
