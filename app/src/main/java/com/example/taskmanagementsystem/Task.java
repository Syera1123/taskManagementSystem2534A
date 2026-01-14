package com.example.taskmanagementsystem;

import com.google.gson.annotations.SerializedName;

public class Task {
    private int id; // Needed for Update/Delete
    private String title;
    private String description;
    @SerializedName("create_date")
    private String createDate;
    private String status;
    @SerializedName("assigned_to")
    private String assignedTo;

    // Constructor used when creating a new task
    public Task(String title, String description, String createDate, String status, String assignedTo) {
        this.title = title;
        this.description = description;
        this.createDate = createDate;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreateDate() { return createDate; }
    public void setCreateDate(String createDate) { this.createDate = createDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
}