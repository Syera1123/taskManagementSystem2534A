package com.example.taskmanagementsystem.remote;

public class ApiUtils {
    public static final String BASE_URL = "https://aptitude.my/taskmanagement/api/";

    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static TaskService getTaskService() {
        return RetrofitClient.getClient(BASE_URL).create(TaskService.class);

    }
}
