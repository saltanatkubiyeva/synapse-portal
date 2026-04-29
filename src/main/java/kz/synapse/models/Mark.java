package kz.synapse.models;

public class Mark {
    private double att1;
    private double att2;
    private double finalMark;

    public Mark(double att1, double att2, double finalMark) {
        this.att1 = att1;
        this.att2 = att2;
        this.finalMark = finalMark;
    }

    public double getTotal() {
        return att1 + att2 + finalMark;
    }

    public String getLetterGrade() {
        double total = getTotal();
        if (total >= 90) return "A";
        if (total >= 80) return "B";
        if (total >= 70) return "C";
        if (total >= 60) return "D";
        return "F";
    }

    public boolean isPassed() {
        return getTotal() >= 60;
    }

    public double getAtt1() {
        return att1;
    }

    public void setAtt1(double att1) {
        this.att1 = att1;
    }

    public double getAtt2() {
        return att2;
    }

    public void setAtt2(double att2) {
        this.att2 = att2;
    }

    public double getFinalMark() {
        return finalMark;
    }

    public void setFinalMark(double finalMark) {
        this.finalMark = finalMark;
    }
}
