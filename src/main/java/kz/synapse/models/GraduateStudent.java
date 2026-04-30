package kz.synapse.models;

import kz.synapse.enums.*;
import kz.synapse.exceptions.*;
import java.util.ArrayList;
import java.util.List;
import kz.synapse.interfaces.Researcher;

public class GraduateStudent extends Student {

    private GraduateDegree degree;
    private Researcher supervisor;
    private List<ResearchPaper> diplomaProjects = new ArrayList<>();
    private TeachingAssistant teachingAssistant;

    public GraduateStudent(String id, String name, String email,
                           String password, Language language,
                           School school, GraduateDegree degree) {
        super(id, name, email, password, language, school);
        this.degree = degree;
    }

    public void addDiplomaProject(ResearchPaper paper) {
        this.diplomaProjects.add(paper);
    }

    public List<ResearchPaper> getDiplomaProjects() {
        return diplomaProjects;
    }

    public void setSupervisor(Researcher supervisor) {
        if (supervisor.calculateHIndex() < 3)
            throw new LowHIndexException();
        this.supervisor = supervisor;
    }

    public Researcher getSupervisor() {
        return supervisor;
    }

    public void assignAsTA(Course course) {
        this.teachingAssistant = new TeachingAssistant(course);
    }

    public void removeTA() {
        this.teachingAssistant = null;
    }

    public boolean isTA() {
        return this.teachingAssistant != null;
    }

    public TeachingAssistant getTeachingAssistant() {
        return teachingAssistant;
    }

    public GraduateDegree getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return String.format(
                "GraduateStudent{name='%s', degree=%s, school=%s, isTA=%b}",
                getName(), degree, getSchool(), isTA()
        );
    }
}