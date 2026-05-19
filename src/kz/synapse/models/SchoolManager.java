package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SchoolManager extends Manager {

    private static final long serialVersionUID = 1L;

    private School school;

    public SchoolManager(String id, String name, String email,
                         String password, Language language,
                         School school) {
        super(id, name, email, password, language, ManagerType.SCHOOL);
        this.school = school;
    }

    // courseOffering

    /** создаёт офферинг дисциплины в семестре. */
    public CourseOffering createCourseOffering(Course course,
                                               SemesterType semester,
                                               int maxStudents) {
        CourseOffering offering = new CourseOffering(
                course, semester, this.school, maxStudents);
        Database.getInstance().addCourseOffering(offering);
        logAction("Created offering: " + course.getName()
                + " [" + semester + "]");
        return offering;
    }

    /** автоматически добавляет дисциплину в список курсов учителя. */
    public void assignHeadLecturer(CourseOffering offering, Teacher teacher) {
        offering.setHeadLecturer(teacher);
        logAction("Assigned head lecturer " + teacher.getName()
                + " to " + offering.getCourse().getName());
    }

    /** добавляет слот расписания в офферинг. */
    public void addSlot(CourseOffering offering, LessonSlot slot) {
        Schedule schedule = Database.getInstance().getSchedule();

        if (schedule.hasRoomConflict(slot))
            throw new IllegalStateException(
                    "Room conflict: " + slot.getRoom()
                            + " on " + slot.getDayOfWeek()
                            + " " + slot.getStartTime() + "–" + slot.getEndTime());

        if (schedule.hasTeacherConflict(slot))
            throw new IllegalStateException(
                    "Teacher conflict: " + slot.getTeacher().getName()
                            + " is already teaching at that time");

        offering.addSlot(slot);

        // учитель слота получает курс в свой список
        slot.getTeacher().assignCourse(offering.getCourse());

        logAction("Added slot [" + slot.getType() + "] "
                + slot.getDayOfWeek() + " " + slot.getStartTime()
                + " teacher=" + slot.getTeacher().getName());
    }

    // офферинг
    public void publishOffering(CourseOffering offering) {
        Database.getInstance().publishOffering(offering);
        addNews(
                "New course available: " + offering.getCourse().getName(),
                offering.getCourse().getName() + " [" + offering.getSemester()
                        + "] open for registration. "
                        + offering.getMaxStudents() + " spots.",
                NewsType.ANNOUNCEMENT
        );
        logAction("Published offering: " + offering.getCourse().getName());
    }

    // teaching Assistant

    public TeachingAssistant assignTA(GraduateStudent gs,
                                      CourseOffering offering,
                                      Teacher assistedTeacher) {
        if (!offering.isTeacherOfOffering(assistedTeacher))
            throw new IllegalArgumentException(
                    assistedTeacher.getName()
                            + " does not teach in this offering");
        gs.assignAsTA(offering, assistedTeacher);
        logAction("Assigned " + gs.getName() + " as TA for "
                + assistedTeacher.getName() + " in "
                + offering.getCourse().getName());
        return gs.getTeachingAssistant();
    }

    public void removeTA(GraduateStudent gs) {
        if (!gs.isTA())
            throw new IllegalStateException(gs.getName() + " is not a TA");
        gs.removeTA();
        logAction("Removed TA role from " + gs.getName());
    }

    // фильтрация по школе

    @Override
    public List<Student> viewStudents(Comparator<Student> c) {
        return Database.getInstance().getAllStudents().stream()
                .filter(s -> s.getSchool() == this.school)
                .sorted(c).collect(Collectors.toList());
    }

    @Override
    public List<Teacher> viewTeachers(Comparator<Teacher> c) {
        return Database.getInstance().getAllTeachers().stream()
                .filter(t -> t.getSchool() == this.school)
                .sorted(c).collect(Collectors.toList());
    }

    @Override
    public String createPerformanceReport() {
        List<Student> students = Database.getInstance().getAllStudents().stream()
                .filter(s -> s.getSchool() == this.school)
                .collect(Collectors.toList());
        double avg = students.stream()
                .mapToDouble(Student::getGpa).average().orElse(0.0);
        logAction("Created performance report for school: " + school);
        return "--- SCHOOL PERFORMANCE REPORT: " + school + " ---\n"
                + "Total Students: " + students.size() + "\n"
                + "Average GPA:    " + String.format("%.2f", avg) + "\n"
                + "Generated by:   " + getName();
    }

    public School getSchool()            { return school; }
    public void setSchool(School school) { this.school = school; }

    @Override
    public String toString() {
        return String.format("SchoolManager{name='%s', school=%s}", getName(), school);
    }
}
