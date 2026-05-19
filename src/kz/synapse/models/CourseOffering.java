package kz.synapse.models;

import kz.synapse.enums.CourseType;
import kz.synapse.enums.LessonType;
import kz.synapse.enums.School;
import kz.synapse.enums.SemesterType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** офферинг в семестре */
public class CourseOffering implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Course course;
    private final SemesterType semester;
    private School targetSchool;
    private int maxStudents;             // общий лимит на офферинг

    // студенты зачисленные на дисциплину (после APPROVED)
    private final Set<Student> enrolledStudents = new HashSet<>();

    // слоты расписания (создаёт SchoolManager)
    private final List<LessonSlot> slots = new ArrayList<>();

    // оценки
    private final Map<Student, Mark> marks = new HashMap<>();

    // посещаемость (общая на офферинг)
    private final Attendance attendance;

    // назначается SchoolManager, может меняться каждый семестр
    private Teacher headLecturer;

    public CourseOffering(Course course, SemesterType semester,
                          School targetSchool, int maxStudents) {
        this.course       = course;
        this.semester     = semester;
        this.targetSchool = targetSchool;
        this.maxStudents  = maxStudents;
        this.attendance   = new Attendance(course);
    }

    // тип курса для студента

    public CourseType getCourseTypeFor(Student student) {
        if (targetSchool == null || student.getSchool() == targetSchool)
            return course.getDefaultType();
        if (course.getDefaultType() == CourseType.MAJOR)
            return CourseType.FREE_ELECTIVE;
        return course.getDefaultType();
    }

    // студенты — Этап 1: зачисление на дисциплину

    public void enrollStudent(Student student) {
        if (!hasSpots())
            throw new IllegalStateException(
                    "CourseOffering «" + course.getName() + "» is full");
        enrolledStudents.add(student);
    }

    public void unenrollStudent(Student student) { enrolledStudents.remove(student); }
    public boolean hasSpots()                    { return enrolledStudents.size() < maxStudents; }
    public boolean isEnrolled(Student student)   { return enrolledStudents.contains(student); }

    // слоты расписания

    public void addSlot(LessonSlot slot)    { slots.add(slot); }
    public void removeSlot(LessonSlot slot) { slots.remove(slot); }

    /** все слоты конкретного типа (lecture или practice). */
    public List<LessonSlot> getSlotsByType(LessonType type) {
        List<LessonSlot> result = new ArrayList<>();
        for (LessonSlot s : slots)
            if (s.getType() == type) result.add(s);
        return result;
    }

    /** все слоты конкретного учителя в этом офферинге. */
    public List<LessonSlot> getSlotsByTeacher(Teacher teacher) {
        List<LessonSlot> result = new ArrayList<>();
        for (LessonSlot s : slots)
            if (s.getTeacher().equals(teacher)) result.add(s);
        return result;
    }

    // оценки

    public Mark getOrCreateMark(Student student) {
        return marks.computeIfAbsent(student, s -> new Mark());
    }

    public Mark getMark(Student student)       { return marks.get(student); }
    public Map<Student, Mark> getAllMarks() {
        return Collections.unmodifiableMap(marks);
    }

    // права на finalExam

    /** только headlecturer может выставлять финальный экзамен. */
    public boolean canSetFinalExam(Teacher teacher) {
        return teacher.equals(headLecturer);
    }

    /** любой учитель который ведёт слот в этом офферинге может ставить баллы. */
    public boolean isTeacherOfOffering(Teacher teacher) {
        return slots.stream().anyMatch(s -> s.getTeacher().equals(teacher));
    }

    // геттеры / сеттеры

    public Course getCourse()                  { return course; }
    public SemesterType getSemester()          { return semester; }
    public School getTargetSchool()            { return targetSchool; }
    public int getMaxStudents()                { return maxStudents; }
    public void setMaxStudents(int max)        { this.maxStudents = max; }
    public Set<Student> getEnrolledStudents() {
        return Collections.unmodifiableSet(enrolledStudents);
    }
    public List<LessonSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }
    public Attendance getAttendance()          { return attendance; }
    public Teacher getHeadLecturer()           { return headLecturer; }
    public void setHeadLecturer(Teacher t)     {
        this.headLecturer = t;
        // headLecturer автоматически добавляется в список курсов учителя
        if (t != null)
            t.assignCourse(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseOffering)) return false;
        CourseOffering co = (CourseOffering) o;
        return Objects.equals(course, co.course)
                && semester == co.semester
                && targetSchool == co.targetSchool;
    }

    @Override
    public int hashCode() { return Objects.hash(course, semester, targetSchool); }

    @Override
    public String toString() {
        return String.format(
                "CourseOffering{%s | %s | %s | %d/%d | head=%s | slots=%d}",
                course.getCourseCode(), course.getName(), semester,
                enrolledStudents.size(), maxStudents,
                headLecturer != null ? headLecturer.getName() : "—",
                slots.size());
    }
}
