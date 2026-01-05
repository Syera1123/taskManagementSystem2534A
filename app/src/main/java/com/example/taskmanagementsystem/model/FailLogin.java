package com.example.taskmanagementsystem.model;

public class FailLogin {
    // fail login
    private String status;
    private com.example.taskmanagementsystem.model.Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.example.taskmanagementsystem.model.Error getError() {
        return error;
    }

    public void setError(com.example.taskmanagementsystem.model.Error error) {
        this.error = error;
    }

}



