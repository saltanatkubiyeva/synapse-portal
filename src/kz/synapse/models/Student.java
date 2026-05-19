package kz.synapse.models;

import kz.synapse.utils.LanguageManager;

import kz.synapse.database.Database;
import kz.synapse.enums.*;
import kz.synapse.exceptions.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Student extends User implements Comparable<Student>, Serializable {

    private static final long serialVersionUID = 1L;

    private int semesterCredits;
    private double gpa;
    private String status;
    private School school;
    private int yearOfStudy;

    private Map<Course, Integer>        courseFails       = new HashMap<>();
    private List<CourseOffering>        enrolledOfferings = new ArrayList<>();
    private Map<CourseOffering, List<LessonSlot>> chosenSlots = new HashMap<>();
    private Set<StudentOrganization>    organizations     = new HashSet<>();
    private final Set<String>           ratedTeacherIds   = new HashSet<>();
    private Transcript                  transcript;

    public Student(String id, String name, String email,
                   String password, Language language, School school) {
        super(id, name, email, password, language);
        this.school         = school;
        this.yearOfStudy    = 1;
        this.semesterCredits= 0;
        this.gpa            = 0.0;
        this.status         = "Active";
        this.transcript     = new Transcript(this);
    }

    public Student(String id, String name, String email,
                   String password, Language language,
                   School school, int yearOfStudy) {
        this(id, name, email, password, language, school);
        this.yearOfStudy = yearOfStudy;
    }

    public void registerForOffering(CourseOffering offering) {
        kz.synapse.services.RegistrationService.getInstance().register(this, offering);
        logAction("Requested registration: " + offering.getCourse().getCourseCode()
                + " [" + offering.getSemester() + "]");
    }

    public void chooseSlot(CourseOffering offering, LessonSlot slot) {
        kz.synapse.services.RegistrationService.getInstance().chooseSlot(this, offering, slot);
    }

    public List<LessonSlot> getChosenSlots(CourseOffering offering) {
        return chosenSlots.getOrDefault(offering, new ArrayList<>());
    }

    public List<LessonSlot> getAllChosenSlots() {
        return chosenSlots.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void addChosenSlot(CourseOffering offering, LessonSlot slot) {
        chosenSlots.computeIfAbsent(offering, o -> new ArrayList<>()).add(slot);
    }

    public boolean slotsComplete(CourseOffering offering) {
        List<LessonSlot> chosen = getChosenSlots(offering);
        long lectures  = chosen.stream().filter(s -> s.getType() == LessonType.LECTURE).count();
        long practices = chosen.stream().filter(s -> s.getType() == LessonType.PRACTICE).count();
        return lectures  == offering.getCourse().getLecturesPerWeek()
                && practices == offering.getCourse().getPracticesPerWeek();
    }

    public List<CourseOffering> viewAvailableOfferings() {
        return Database.getInstance().getPublishedOfferings().stream()
                .filter(o -> o.getSemester() == Database.getInstance().getCurrentSemester())
                .filter(CourseOffering::hasSpots)
                .collect(Collectors.toList());
    }

    public Mark getMark(CourseOffering offering) { return offering.getMark(this); }

    public void viewMarks() {
        boolean any = false;
        for (CourseOffering off : enrolledOfferings) {
            Mark mark = getMark(off);
            if (mark == null) continue;
            any = true;
            System.out.printf("%-30s [%s] | %s (%.1f)%n",
                    off.getCourse().getName(), off.getSemester(),
                    mark.getLetterGrade(), mark.getTotal());
        }
        if (!any) System.out.println(LanguageManager.get("models.Student.no.marks.yet"));
    }

    public String getTranscript() {
        logAction("Viewed transcript");
        return transcript.toString();
    }

    public void updateTranscript() {
        for (CourseOffering off : enrolledOfferings) {
            Mark mark = getMark(off);
            if (mark == null) continue;
            if (transcript.getRecords().containsKey(off)) continue;
            Course course = off.getCourse();
            if (mark.isPassed()) {
                transcript.addRecord(off, mark);
            } else {
                int fails = courseFails.getOrDefault(course, 0) + 1;
                courseFails.put(course, fails);
                addNotification(new Notification("You failed: " + course.getName()
                        + " (Attempt " + fails + "/3)"));
            }
        }
        this.gpa = transcript.calculateGpa();
        logAction("Transcript updated");
    }

    public List<String> checkFailLimits() {
        List<String> violations = new ArrayList<>();
        for (Map.Entry<Course, Integer> entry : courseFails.entrySet())
            if (entry.getValue() >= 3) violations.add(entry.getKey().getName());
        return violations;
    }

    public void rateTeacher(Teacher teacher, int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be 1-5");
        if (ratedTeacherIds.contains(teacher.getId()))
            throw new IllegalStateException("You have already rated " + teacher.getName());
        teacher.addRating(rating);
        ratedTeacherIds.add(teacher.getId());
        logAction("Rated teacher: " + teacher.getName() + " → " + rating);
    }

    public boolean hasRated(Teacher teacher) {
        return ratedTeacherIds.contains(teacher.getId());
    }

    public void viewTeacherInfo(Teacher teacher) { System.out.println(teacher); }

    public void proposeOrganization(String name, String description) {
        OrganizationProposal proposal = new OrganizationProposal(name, description, this);
        Database.getInstance().addOrgProposal(proposal);
        logAction("Proposed organization: " + name);
    }

    public void requestJoinOrganization(StudentOrganization org) {
        org.requestJoin(this);
        logAction("Requested to join organization: " + org.getName());
    }

    public void leaveOrganization(StudentOrganization org) {
        org.leave(this);
        organizations.remove(org);
        logAction("Left organization: " + org.getName());
    }

    public void leaveOrganizationInternal(StudentOrganization org) {
        organizations.remove(org);
    }

    public void transferLeadership(StudentOrganization org, Student successor) {
        if (!org.isMember(this) || !org.getHead().equals(this))
            throw new IllegalStateException("You are not the head of " + org.getName());
        org.transferLeadership(successor);
        logAction("Transferred leadership of " + org.getName() + " to " + successor.getName());
    }

    public void addToOrganization(StudentOrganization org) {
        organizations.add(org);
    }

    public Set<StudentOrganization> getOrganizations() {
        return Collections.unmodifiableSet(organizations);
    }

    public void enrollInOffering(CourseOffering offering) {
        if (!enrolledOfferings.contains(offering)) enrolledOfferings.add(offering);
    }

    public void clearSemesterEnrollment() {
        enrolledOfferings.clear();
        chosenSlots.clear();
        semesterCredits = 0;
    }

    public int getFailCount(Course course) { return courseFails.getOrDefault(course, 0); }

    @Override
    public int compareTo(Student other) { return Double.compare(other.gpa, this.gpa); }

    public int getSemesterCredits()              { return semesterCredits; }
    public void setSemesterCredits(int c)        { this.semesterCredits = c; }
    public void addSemesterCredits(int c)        { this.semesterCredits += c; }
    public double getGpa()                       { return gpa; }
    public String getStatus()                    { return status; }
    public void setStatus(String status)         { this.status = status; }
    public School getSchool()                    { return school; }
    public int getYearOfStudy()                  { return yearOfStudy; }
    public void setYearOfStudy(int year)         { this.yearOfStudy = year; }
    public Map<Course, Integer> getCourseFails() { return Collections.unmodifiableMap(courseFails); }
    public List<CourseOffering> getEnrolledOfferings() { return Collections.unmodifiableList(enrolledOfferings); }
    public List<CourseOffering> getEnrolledCourses()   { return getEnrolledOfferings(); }
    public Transcript getTranscriptObject()            { return transcript; }
    public Set<Course> getPassedCourses()              { return new HashSet<>(transcript.getPassedCourses()); }

    @Override
    public String toString() {
        return String.format("Student{name='%s', school=%s, year=%d, gpa=%.2f, credits=%d}",
                getName(), school, yearOfStudy, gpa, semesterCredits);
    }
}
