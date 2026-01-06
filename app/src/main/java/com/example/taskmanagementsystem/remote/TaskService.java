package com.example.taskmanagementsystem.remote;

import com.example.taskmanagementsystem.model.TaskList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
public interface TaskService {
    @GET("tasks")
    Call<List<TaskList>> getAllTask(@Header("api-key") String api_key);
}
