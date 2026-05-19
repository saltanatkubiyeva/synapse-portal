package kz.synapse.services;

import kz.synapse.database.Database;
import kz.synapse.enums.LessonType;
import kz.synapse.enums.RegistrationStatus;
import kz.synapse.enums.SemesterType;
import kz.synapse.exceptions.*;
import kz.synapse.models.*;

public class RegistrationService {

    private static final RegistrationService INSTANCE = new RegistrationService();
    static final int MAX_CREDITS_PER_SEMESTER = 21;

    private RegistrationService() {}

    public static RegistrationService getInstance() { return INSTANCE; }

    public void register(Student student, CourseOffering offering) {
        if (!Database.getInstance().isRegistrationOpen())
            throw new IllegalStateException("Registration is currently closed");
        if (offering.getSemester() != Database.getInstance().getCurrentSemester())
            throw new IllegalStateException("This offering is not available for the current semester");
        if (getSemesterPendingCredits(student) + offering.getCourse().getCredits() > MAX_CREDITS_PER_SEMESTER)
            throw new MaxCreditsException();
        for (Course prereq : offering.getCourse().getPrerequisites()) {
            if (!student.getPassedCourses().contains(prereq))
                throw new PrerequisiteNotMetException(offering.getCourse().getCourseCode());
        }
        if (student.getFailCount(offering.getCourse()) >= 3)
            throw new CourseFailLimitException(offering.getCourse().getCourseCode());
        if (!offering.hasSpots())
            throw new IllegalStateException("No spots left in «" + offering.getCourse().getName() + "»");
        if (hasRegistrationFor(student, offering))
            throw new IllegalStateException("Already applied for «" + offering.getCourse().getName() + "»");

        CourseRegistration reg = new CourseRegistration(student, offering);
        Database.getInstance().addPendingRegistration(reg);
    }

    public void chooseSlot(Student student, CourseOffering offering, LessonSlot slot) {
        if (!offering.isEnrolled(student))
            throw new IllegalStateException(
                    "You are not approved for «" + offering.getCourse().getName() + "». Complete Step 1 first.");
        if (!offering.getSlots().contains(slot))
            throw new IllegalArgumentException(
                    "This slot does not belong to «" + offering.getCourse().getName() + "»");
        if (slot.isEnrolled(student))
            throw new IllegalStateException("Already enrolled in this slot");

        long alreadyChosen = student.getChosenSlots(offering).stream()
                .filter(s -> s.getType() == slot.getType()).count();
        int required = slot.getType() == LessonType.LECTURE
                ? offering.getCourse().getLecturesPerWeek()
                : offering.getCourse().getPracticesPerWeek();
        if (alreadyChosen >= required)
            throw new IllegalStateException(
                    "You already have " + alreadyChosen + " " + slot.getType()
                            + " slot(s). Required: " + required);
        if (!slot.hasSpots())
            throw new IllegalStateException("No spots left in this slot: " + slot);

        for (LessonSlot chosen : student.getAllChosenSlots()) {
            if (chosen.overlapsWith(slot))
                throw new IllegalStateException(
                        "Time conflict: " + slot.getDayOfWeek()
                                + " " + slot.getStartTime() + "–" + slot.getEndTime()
                                + " overlaps with " + chosen);
        }

        slot.enrollStudent(student);
        student.addChosenSlot(offering, slot);

        if (student.slotsComplete(offering)) {
            findRegistration(student, offering).ifPresent(r -> {
                r.setStatus(RegistrationStatus.REGISTERED);
                student.addSemesterCredits(offering.getCourse().getCredits());
            });
        }
    }

    private java.util.Optional<CourseRegistration> findRegistration(Student student, CourseOffering offering) {
        return Database.getInstance().getApprovedRegistrations().stream()
                .filter(r -> r.getStudent().equals(student) && r.getOffering().equals(offering))
                .findFirst();
    }

    private static boolean hasRegistrationFor(Student student, CourseOffering offering) {
        if (offering.isEnrolled(student)) return true;
        Database db = Database.getInstance();
        java.util.function.Predicate<CourseRegistration> same =
                r -> r.getStudent().equals(student) && r.getOffering().equals(offering);
        return db.getPendingRegistrations().stream().anyMatch(same)
                || db.getApprovedRegistrations().stream().anyMatch(same);
    }

    static int getSemesterPendingCredits(Student student) {
        SemesterType semester = Database.getInstance().getCurrentSemester();
        Database db = Database.getInstance();
        int total = 0;
        for (CourseRegistration r : db.getPendingRegistrations()) {
            if (r.getStudent().equals(student)
                    && r.getOffering().getSemester() == semester
                    && r.getStatus() == RegistrationStatus.PENDING)
                total += r.getOffering().getCourse().getCredits();
        }
        for (CourseRegistration r : db.getApprovedRegistrations()) {
            if (r.getStudent().equals(student)
                    && r.getOffering().getSemester() == semester)
                total += r.getOffering().getCourse().getCredits();
        }
        total += student.getSemesterCredits();
        return total;
    }
}
