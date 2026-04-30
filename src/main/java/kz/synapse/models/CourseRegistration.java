package kz.synapse.models;

import kz.synapse.enums.RegistrationStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class CourseRegistration implements Serializable {

    private Student student;
    private Course course;
    private RegistrationStatus status;
    private LocalDateTime createdAt;

    public CourseRegistration(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.status = RegistrationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approve() { this.status = RegistrationStatus.APPROVED; }
    public void reject()  { this.status = RegistrationStatus.REJECTED; }

    public Student getStudent()           { return student; }
    public Course getCourse()             { return course; }
    public RegistrationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    @Override
    public String toString() {
        return String.format(
                "CourseRegistration{student='%s', course='%s', status=%s, createdAt=%s}",
                student.getName(), course.getName(), status, createdAt
        );
    }
}