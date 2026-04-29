package kz.synapse.models;

import kz.synapse.enums.CitationFormat;
import java.time.LocalDate;
import java.util.List;

public class ResearchPaper implements Comparable<ResearchPaper> {
    private String title;
    private List<String> authors;
    private String journal;
    private int citations;
    private LocalDate date;
    private String doi;
    private int pages;

    public ResearchPaper(String title, List<String> authors, String journal, int citations, LocalDate date, String doi, int pages) {
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.citations = citations;
        this.date = date;
        this.doi = doi;
        this.pages = pages;
    }

    public String getCitation(CitationFormat format) {
        if (format == CitationFormat.PLAIN_TEXT) {
            return authors + ". " + title + ". " + journal + ", " + date.getYear() + ".";
        } else if (format == CitationFormat.BIBTEX) {
            return "@article{" + doi + ",\n  title={" + title + "},\n  journal={" + journal + "},\n  year={" + date.getYear() + "}\n}";
        }
        return "";
    }

    @Override
    public int compareTo(ResearchPaper other) {
        return Integer.compare(other.citations, this.citations);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
