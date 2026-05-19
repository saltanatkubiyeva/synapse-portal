package kz.synapse.models;

import kz.synapse.enums.AttestationPeriod;
import kz.synapse.enums.LessonType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class LessonScore implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private final LessonType lessonType;      
    private final AttestationPeriod period;  
    private final double score;               
    private final String comment;            

    public LessonScore(LocalDate date, LessonType lessonType,
                       AttestationPeriod period, double score, String comment) {
        if (score < 0)
            throw new IllegalArgumentException("Score cannot be negative");
        this.date       = date;
        this.lessonType = lessonType;
        this.period     = period;
        this.score      = score;
        this.comment    = comment != null ? comment : "";
    }

    public LocalDate getDate()              { return date; }
    public LessonType getLessonType()       { return lessonType; }
    public AttestationPeriod getPeriod()    { return period; }
    public double getScore()                { return score; }
    public String getComment()              { return comment; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonScore)) return false;
        LessonScore s = (LessonScore) o;
        return Objects.equals(date, s.date)
                && lessonType == s.lessonType
                && period == s.period;
    }

    @Override
    public int hashCode() { return Objects.hash(date, lessonType, period); }

    @Override
    public String toString() {
        return String.format("%-12s | %-8s | %5.2f | %s",
                date, lessonType, score,
                comment.isEmpty() ? "—" : comment);
    }
}
