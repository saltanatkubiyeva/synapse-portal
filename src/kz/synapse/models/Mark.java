package kz.synapse.models;

import kz.synapse.enums.AttestationPeriod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import kz.synapse.utils.LanguageManager;

import java.util.List;

public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<LessonScore> lessonScores = new ArrayList<>();

    private double finalExam = 0.0;

    public Mark() {}

    public void addLessonScore(LessonScore score) {
        if (score.getPeriod() == AttestationPeriod.FINAL)
            throw new IllegalArgumentException(
                    "Use setFinalExam() for final exam score, not addLessonScore()");
        lessonScores.add(score);
    }

    public double getAtt1() {
        return lessonScores.stream()
                .filter(s -> s.getPeriod() == AttestationPeriod.ATT1)
                .mapToDouble(LessonScore::getScore)
                .sum();
    }

    public double getAtt2() {
        return lessonScores.stream()
                .filter(s -> s.getPeriod() == AttestationPeriod.ATT2)
                .mapToDouble(LessonScore::getScore)
                .sum();
    }

    public double getFinalExam() { return finalExam; }

    public void setFinalExam(double finalExam) {
        if (finalExam < 0 || finalExam > 40)
            throw new IllegalArgumentException("Final exam score must be 0–40");
        this.finalExam = finalExam;
    }

    public double getTotal() {
        return getAtt1() + getAtt2() + finalExam;
    }

    public boolean isPassed() { return getTotal() >= 50.0; }

    public String getLetterGrade() {
        double t = getTotal();
        if (t >= 95) return "A";
        if (t >= 90) return "A-";
        if (t >= 85) return "B+";
        if (t >= 80) return "B";
        if (t >= 75) return "B-";
        if (t >= 70) return "C+";
        if (t >= 65) return "C";
        if (t >= 60) return "C-";
        if (t >= 55) return "D+";
        if (t >= 50) return "D";
        return "F";
    }

    public double getGpaEquivalent() {
        double t = getTotal();
        if (t >= 95) return 4.0;
        if (t >= 90) return 3.67;
        if (t >= 85) return 3.33;
        if (t >= 80) return 3.0;
        if (t >= 75) return 2.67;
        if (t >= 70) return 2.33;
        if (t >= 65) return 2.0;
        if (t >= 60) return 1.67;
        if (t >= 55) return 1.33;
        if (t >= 50) return 1.0;
        return 0.0;
    }

    public List<LessonScore> getLessonScores() {
        return Collections.unmodifiableList(lessonScores);
    }

    public void printJournal() {
        System.out.println(String.format("%-12s | %-8s | %5s | %s",
                LanguageManager.get("journal.col.date"),
                LanguageManager.get("journal.col.lesson"),
                LanguageManager.get("journal.col.score"),
                LanguageManager.get("journal.col.comment")));
        System.out.println(LanguageManager.get("journal.separator.short"));
        lessonScores.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .forEach(System.out::println);
        System.out.println(LanguageManager.get("journal.separator.short"));
        System.out.printf("ATT1: %.2f | ATT2: %.2f | Final: %.2f | Total: %.2f (%s)%n",
                getAtt1(), getAtt2(), finalExam, getTotal(), getLetterGrade());
    }

    @Override
    public String toString() {
        return String.format(
                "Mark{att1=%.2f, att2=%.2f, final=%.2f | total=%.2f (%s) | passed=%b}",
                getAtt1(), getAtt2(), finalExam, getTotal(), getLetterGrade(), isPassed());
    }
}
