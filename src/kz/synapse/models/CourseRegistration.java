package kz.synapse.models;

import kz.synapse.enums.RegistrationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/** заявка в courseoffering. */
public class CourseRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Student student;
    private final CourseOffering offering;
    private RegistrationStatus status;
    private final LocalDateTime createdAt;

    public CourseRegistration(Student student, CourseOffering offering) {
        this.student   = student;
        this.offering  = offering;
        this.status    = RegistrationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approve() { this.status = RegistrationStatus.APPROVED; }
    public void reject()  { this.status = RegistrationStatus.REJECTED; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public Student getStudent()             { return student; }
    public CourseOffering getOffering()     { return offering; }
    /** курс офферинга */
    public Course getCourse()              { return offering.getCourse(); }
    public RegistrationStatus getStatus()  { return status; }
    public LocalDateTime getCreatedAt()    { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseRegistration)) return false;
        CourseRegistration r = (CourseRegistration) o;
        return Objects.equals(student, r.student)
                && Objects.equals(offering, r.offering);
    }

    @Override
    public int hashCode() { return Objects.hash(student, offering); }

    @Override
    public String toString() {
        return String.format("CourseRegistration{student='%s', offering='%s', status=%s}",
                student.getName(), offering.getCourse().getName(), status);
    }
}
