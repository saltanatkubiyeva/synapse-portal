package kz.synapse.models;

import kz.synapse.enums.*;
import kz.synapse.exceptions.*;
import kz.synapse.interfaces.Researcher;

import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student {

    private static final long serialVersionUID = 1L;

    private final GraduateDegree degree;

    // научный руководитель — Researcher с h-index ≥ 3
    private Researcher supervisor;

    private List<ResearchPaper> diplomaProjects = new ArrayList<>();

    // teaching Assistant — опциональная роль (null = не TA)
    private TeachingAssistant teachingAssistant;

    public GraduateStudent(String id, String name, String email,
                           String password, Language language,
                           School school, GraduateDegree degree) {
        super(id, name, email, password, language, school);
        this.degree = degree;
    }

    // supervisor

    public void setSupervisor(Researcher supervisor) {
        if (supervisor.calculateHIndex() < 3)
            throw new LowHIndexException();
        this.supervisor = supervisor;
        logAction("Supervisor assigned: " + supervisor.getName());
    }

    public void changeSupervisor(Researcher newSupervisor) {
        if (newSupervisor.calculateHIndex() < 3)
            throw new LowHIndexException();
        String old = this.supervisor != null ? this.supervisor.getName() : "none";
        this.supervisor = newSupervisor;
        logAction("Supervisor changed: " + old + " → " + newSupervisor.getName());
    }

    public Researcher getSupervisor() { return supervisor; }

    // diploma Projects

    public void addDiplomaProject(ResearchPaper paper) {
        diplomaProjects.add(paper);
        logAction("Added diploma project: " + paper.getTitle());
    }

    public List<ResearchPaper> getDiplomaProjects() { return diplomaProjects; }

    // teaching Assistant — назначается SchoolManager (SRP)

    /** назначает роль ta для конкретного courseoffering. */
    void assignAsTA(CourseOffering offering, Teacher assistedTeacher) {
        this.teachingAssistant = new TeachingAssistant(this, offering, assistedTeacher);
        logAction("Assigned as TA for: " + offering.getCourse().getName());
    }

    void removeTA() {
        logAction("TA role removed from: "
                + (teachingAssistant != null
                ? teachingAssistant.getOffering().getCourse().getName()
                : "—"));
        this.teachingAssistant = null;
    }

    public boolean isTA()                              { return teachingAssistant != null; }
    public TeachingAssistant getTeachingAssistant()    { return teachingAssistant; }
    public GraduateDegree getDegree()                  { return degree; }

    @Override
    public String toString() {
        return String.format(
                "GraduateStudent{name='%s', degree=%s, school=%s, isTA=%b, supervisor=%s}",
                getName(), degree, getSchool(), isTA(),
                supervisor != null ? supervisor.getName() : "none");
    }
}
