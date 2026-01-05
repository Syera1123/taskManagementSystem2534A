package com.example.taskmanagementsystem.remote;

public class ApiUtils {
    public static final String BASE_URL = "https://aptitude.my/2024973881/api/";

    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
