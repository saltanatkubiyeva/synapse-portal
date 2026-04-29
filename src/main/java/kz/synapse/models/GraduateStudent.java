package kz.synapse.models;

import kz.synapse.enums.GraduateDegree;
import kz.synapse.interfaces.Researcher;
import java.util.List;

public class GraduateStudent extends Student {
    private GraduateDegree degree;
    private List<ResearchPaper> diplomaProjects;
    private Researcher supervisor;

    public GraduateStudent(Long id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public void setSupervisor(Researcher supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public String toString() {
        return "GraduateStudent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", degree=" + degree +
                ", gpa=" + gpa +
                '}';
    }

    public GraduateDegree getDegree() {
        return degree;
    }

    public void setDegree(GraduateDegree degree) {
        this.degree = degree;
    }

    public List<ResearchPaper> getDiplomaProjects() {
        return diplomaProjects;
    }

    public void setDiplomaProjects(List<ResearchPaper> diplomaProjects) {
        this.diplomaProjects = diplomaProjects;
    }

    public Researcher getSupervisor() {
        return supervisor;
    }
}
