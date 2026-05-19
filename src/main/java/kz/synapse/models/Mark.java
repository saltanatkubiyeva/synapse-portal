package kz.synapse.models;

import java.io.Serializable;

public class Mark implements Serializable {

    // Оценки за каждый период (предполагается 100-балльная шкала для каждого)
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;

    public Mark() {
        this.firstAttestation = 0.0;
        this.secondAttestation = 0.0;
        this.finalExam = 0.0;
    }

    public Mark(double firstAttestation, double secondAttestation, double finalExam) {
        setFirstAttestation(firstAttestation);
        setSecondAttestation(secondAttestation);
        setFinalExam(finalExam);
    }

    public double getTotal() {
        return (firstAttestation * 0.3) + (secondAttestation * 0.3) + (finalExam * 0.4);
    }

    public boolean isPassed() {
        return getTotal() >= 50.0;
    }

    public String getLetterGrade() {
        double total = getTotal();

        if (total >= 95.0) return "A";
        if (total >= 90.0) return "A-";
        if (total >= 85.0) return "B+";
        if (total >= 80.0) return "B";
        if (total >= 75.0) return "B-";
        if (total >= 70.0) return "C+";
        if (total >= 65.0) return "C";
        if (total >= 60.0) return "C-";
        if (total >= 55.0) return "D+";
        if (total >= 50.0) return "D";
        return "F";
    }

    public double getGpaEquivalent() {
        double total = getTotal();

        if (total >= 95.0) return 4.0;
        if (total >= 90.0) return 3.67;
        if (total >= 85.0) return 3.33;
        if (total >= 80.0) return 3.0;
        if (total >= 75.0) return 2.67;
        if (total >= 70.0) return 2.33;
        if (total >= 65.0) return 2.0;
        if (total >= 60.0) return 1.67;
        if (total >= 55.0) return 1.33;
        if (total >= 50.0) return 1.0;
        return 0.0;
    }

    public double getFirstAttestation() { return firstAttestation; }

    public void setFirstAttestation(double firstAttestation) {
        validateMark(firstAttestation);
        this.firstAttestation = firstAttestation;
    }

    public double getSecondAttestation() { return secondAttestation; }

    public void setSecondAttestation(double secondAttestation) {
        validateMark(secondAttestation);
        this.secondAttestation = secondAttestation;
    }

    public double getFinalExam() { return finalExam; }

    public void setFinalExam(double finalExam) {
        validateMark(finalExam);
        this.finalExam = finalExam;
    }

    private void validateMark(double mark) {
        if (mark < 0.0 || mark > 100.0) {
            throw new IllegalArgumentException("Mark must be between 0 and 100");
        }
    }


    @Override
    public String toString() {
        return String.format(
                "Mark{att1=%.1f, att2=%.1f, final=%.1f | Total=%.1f (%s) | Passed=%b}",
                firstAttestation, secondAttestation, finalExam, getTotal(), getLetterGrade(), isPassed()
        );
    }
}