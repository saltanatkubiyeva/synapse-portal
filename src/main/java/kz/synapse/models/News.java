package kz.synapse.models;

import kz.synapse.enums.NewsType;

import java.util.ArrayList;
import java.util.List;

public class News {
    private String topic;
    private String content;
    private NewsType type;
    private boolean isPinned;
    private List<String> comments = new ArrayList<>();

    public News(String topic, NewsType type) {
        this.topic = topic;
        this.content = "";
        this.type = type;
        this.isPinned = false;
    }

    @Override
    public String toString() {
        return "News{" +
                "topic='" + topic + '\'' +
                ", content='" + content + '\'' +
                ", isPinned=" + isPinned +
                '}';
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
