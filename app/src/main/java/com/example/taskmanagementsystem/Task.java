package com.example.taskmanagementsystem;

public class Task {
    private String title;
    private String createDate;
    private String status;
    private String assignedTo;


    public Task(String title, String createDate, String status, String assignedTo) {
        this.title = title;
        this.createDate = createDate;
        this.status = status;
        this.assignedTo = assignedTo;
    }


    public String getTitle() { return title; }
    public String getCreateDate() { return createDate; }
    public String getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }


    public void setTitle(String title) { this.title = title; }
    public void setCreateDate(String createDate) { this.createDate = createDate; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
}