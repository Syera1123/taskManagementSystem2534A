package com.example.taskmanagementsystem.remote;

import com.example.taskmanagementsystem.model.TaskList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TaskService {
    @GET("tasks")
    Call<List<TaskList>> getAllTask(@Header("api-key") String api_key);

    @GET("tasks/{id}")
    Call<TaskList> getTask(@Header("api-key") String api_key, @Path("id") int id);
}
