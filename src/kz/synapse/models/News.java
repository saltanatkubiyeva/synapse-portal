package kz.synapse.models;

import kz.synapse.enums.NewsType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** глобальная новость в публичной ленте */
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private NewsType type;
    private boolean isPinned;
    private LocalDateTime createdAt;
    private List<String> comments = new ArrayList<>();

    // title only
    public News(String title, NewsType type) {
        this.title     = title;
        this.content   = "";
        this.type      = type;
        this.isPinned  = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // full
    public News(String title, String content, NewsType type) {
        this(title, type);
        this.content = content;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    // геттеры / сеттеры
    public String getTitle()               { return title; }
    public void setTitle(String title)     { this.title = title; }
    public String getContent()             { return content; }
    public void setContent(String content) { this.content = content; }
    public NewsType getType()              { return type; }
    public boolean isPinned()              { return isPinned; }
    public void setPinned(boolean pinned)  { this.isPinned = pinned; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public List<String> getComments()      { return comments; }

    // equals / hashCode по title + createdAt — уникальная пара
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;
        News news = (News) o;
        return Objects.equals(title, news.title)
                && Objects.equals(createdAt, news.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, createdAt);
    }

    @Override
    public String toString() {
        return String.format("[%s]%s [%s] %s",
                type,
                isPinned ? " 📌" : "",
                createdAt.toLocalDate(),
                title);
    }
}
