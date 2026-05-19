package kz.synapse.models;

import kz.synapse.enums.AttestationPeriod;
import kz.synapse.enums.LessonType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class TeachingAssistant implements Serializable {

    private static final long serialVersionUID = 1L;

    private final GraduateStudent graduateStudent;
    private final CourseOffering  offering;
    private final Teacher         assistedTeacher; // чьи уроки ведёт

    public TeachingAssistant(GraduateStudent graduateStudent,
                             CourseOffering offering,
                             Teacher assistedTeacher) {
        this.graduateStudent  = graduateStudent;
        this.offering         = offering;
        this.assistedTeacher  = assistedTeacher;
    }

    // доступные слоты — все слоты assistedTeacher в этом офферинге

    public List<LessonSlot> getAccessibleSlots() {
        return offering.getSlotsByTeacher(assistedTeacher);
    }

    // балл за урок — только ATT1/ATT2, не FINAL

    /** ставит балл студенту за конкретное занятие */
    public void addLessonScore(Student student, LessonScore score) {
        checkEnrolled(student);
        checkSlotAccess(score);
        if (score.getPeriod() == AttestationPeriod.FINAL)
            throw new IllegalStateException("TA cannot set final exam scores");
        offering.getOrCreateMark(student).addLessonScore(score);
    }

    /** удобный метод — создать lessonscore и добавить сразу. */
    public void addScore(Student student, LocalDate date, LessonType lessonType,
                         AttestationPeriod period, double score, String comment) {
        addLessonScore(student,
                new LessonScore(date, lessonType, period, score, comment));
    }

    private void checkEnrolled(Student student) {
        if (!offering.isEnrolled(student))
            throw new IllegalArgumentException(
                    student.getName() + " is not enrolled in "
                            + offering.getCourse().getName());
    }

    private void checkSlotAccess(LessonScore score) {
        boolean hasAccess = getAccessibleSlots().stream()
                .anyMatch(s -> s.getType() == score.getLessonType());
        if (!hasAccess)
            throw new IllegalStateException(
                    "TA has no access to " + score.getLessonType()
                            + " slots of " + assistedTeacher.getName());
    }

    public GraduateStudent getGraduateStudent() { return graduateStudent; }
    public CourseOffering getOffering()         { return offering; }
    public Teacher getAssistedTeacher()         { return assistedTeacher; }

    @Override
    public String toString() {
        return String.format("TeachingAssistant{ta='%s', assists='%s', course='%s' [%s]}",
                graduateStudent.getName(), assistedTeacher.getName(),
                offering.getCourse().getName(), offering.getSemester());
    }
}
