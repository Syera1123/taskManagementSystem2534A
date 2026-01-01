package com.example.taskmanagementsystem;

public class Task {
    private String title;
    private String dueDate;
    private String status;
    private String assignedTo;

    public Task(String title, String dueDate, String status, String assignedTo) {
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }
    public void setStatus(String status) { this.status = status; }
}
