package kz.synapse.models;

import kz.synapse.enums.UrgencyLevel;
import java.util.List;

public class Complaint {
    private UrgencyLevel urgency;
    private boolean toDean;
    private String text;
    private Teacher fromTeacher;
    private List<Student> aboutStudents;

    public Complaint(String text, UrgencyLevel urgency) {
        this.text = text;
        this.urgency = urgency;
        this.toDean = false;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public boolean isToDean() {
        return toDean;
    }

    public void setToDean(boolean toDean) {
        this.toDean = toDean;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Teacher getFromTeacher() {
        return fromTeacher;
    }

    public void setFromTeacher(Teacher fromTeacher) {
        this.fromTeacher = fromTeacher;
    }

    public List<Student> getAboutStudents() {
        return aboutStudents;
    }

    public void setAboutStudents(List<Student> aboutStudents) {
        this.aboutStudents = aboutStudents;
    }
}
