package kz.synapse.models;

import kz.synapse.enums.CitationFormat;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ResearchPaper implements Serializable {

    // поля
    private String title;
    private String authors;
    private String journal;
    private int pages;
    private int citations;
    private LocalDate publishedDate;
    private String doi;

    public ResearchPaper(String title, String authors, String journal,
                         int pages, LocalDate publishedDate, String doi) {
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.pages = pages;
        this.publishedDate = publishedDate;
        this.doi = doi;
        this.citations = 0;
    }

    // citation
    public String getCitation(CitationFormat format) {
        if (format == CitationFormat.PLAIN_TEXT) {
            return String.format("%s. (%d). \"%s\". %s. %s",
                    authors, publishedDate.getYear(), title, journal, doi);
        } else if (format == CitationFormat.BIBTEX) {
            String citeKey = authors.split(",")[0]
                    .trim()
                    .replace(" ", "")
                    + publishedDate.getYear();
            return String.format(
                    "@article{%s,\n" +
                            "  author = {%s},\n" +
                            "  title = {%s},\n" +
                            "  journal = {%s},\n" +
                            "  year = {%d},\n" +
                            "  pages = {%d},\n" +
                            "  doi = {%s}\n" +
                            "}",
                    citeKey, authors, title, journal,
                    publishedDate.getYear(), pages, doi);
        }
        return "Unknown format";
    }

    // citations
    public void addCitation()                { this.citations++; }
    public void setCitations(int citations)  {
        if (citations < 0) throw new IllegalArgumentException("Citations cannot be negative");
        this.citations = citations;
    }

    // equals / hashCode по doi
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper p = (ResearchPaper) o;
        return Objects.equals(doi, p.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }

    @Override
    public String toString() {
        return String.format(
                "ResearchPaper{title='%s', citations=%d, pages=%d, published=%s}",
                title, citations, pages, publishedDate);
    }

    // геттеры
    public String getTitle()             { return title; }
    public String getAuthors()           { return authors; }
    public String getJournal()           { return journal; }
    public int getPages()                { return pages; }
    public int getCitations()            { return citations; }
    public LocalDate getPublishedDate()  { return publishedDate; }
    public String getDoi()               { return doi; }
}
