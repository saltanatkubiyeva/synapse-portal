package kz.synapse.models;

import kz.synapse.database.Database;
import kz.synapse.enums.*;
import java.util.List;

public class ORManager extends Manager {

    public ORManager(String id, String name, String email,
                     String password, Language language,
                     double salary) {
        super(id, name, email, password, language, salary, ManagerType.OR);
    }

    public void openSemester(SemesterType semester) {
        Database.getInstance().setCurrentSemester(semester);
        Database.getInstance().setRegistrationOpen(true);

        Database.getInstance().addNews(new News(
                "Registration for " + semester + " is OPEN! " +
                        "Max 21 credits allowed per semester.",
                NewsType.ANNOUNCEMENT
        ));

        logAction("Opened semester: " + semester);
    }

    public void closeSemester() {
        Database.getInstance().setRegistrationOpen(false);

        List<Student> students = Database.getInstance().getAllStudents();

        for (Student student : students) {
            student.updateTranscript();
            student.checkFailLimits();
            student.setCredits(0);
        }

        Database.getInstance().addNews(new News(
                "Semester closed. All marks finalized " +
                        "and transcripts updated.",
                NewsType.ANNOUNCEMENT
        ));

        logAction("Closed the current semester.");
    }

    @Override
    public String toString() {
        return String.format("ORManager{name='%s', email='%s'}",
                getName(), getEmail());
    }
}