package com.bugtracker.model;

import java.time.LocalDateTime;

public class BugComment {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private User author;
    private Bug bug;

    public BugComment() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Bug getBug() { return bug; }
    public void setBug(Bug bug) { this.bug = bug; }
}
