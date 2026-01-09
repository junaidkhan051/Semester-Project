package com.bugtracker.model;

import com.bugtracker.model.enums.Severity;
import com.bugtracker.model.enums.Status;

import java.time.LocalDateTime;

public class Bug {
    private int id;
    private String title;
    private String description;
    private String steps;
    private Severity severity;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User reporter;
    private User assignee;
    private Product product;

    public Bug() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }
    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    @Override
    public String toString() {
        return "#" + id + " - " + title + " [" + (status!=null?status.name():"?") + "]";
    }
}
