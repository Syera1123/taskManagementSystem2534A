package com.example.taskmanagementsystem.model;

public class TaskList {
    private int id;
    private String title;
    private String description;
    private String created_task_by;
    private String assigned_to;
    private String status;
    private String create_date;
    private String finish_date;

    public TaskList(){

    }

    public TaskList(int id, String title, String description, String created_task_by, String assigned_to, String status, String create_date, String finish_date){
        this.id = id;
        this.title = title;
        this.description = description;
        this.created_task_by = created_task_by;
        this.assigned_to = assigned_to;
        this.status = status;
        this.create_date = create_date;
        this.finish_date = finish_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_task_by() {
        return created_task_by;
    }

    public void setCreated_task_by(String created_task_by) {
        this.created_task_by = created_task_by;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }


    @Override
    public String toString() {
        return "TaskList{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", created_task_by='" + created_task_by + '\'' +
                ", assigned_to='" + assigned_to + '\'' +
                ", status='" + status + '\'' +
                ", create_date='" + create_date + '\'' +
                ", finish_date='" + finish_date + '\'' +
                '}';
    }

}
