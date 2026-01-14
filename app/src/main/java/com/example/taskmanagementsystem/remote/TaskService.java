package com.example.taskmanagementsystem.remote;

import com.example.taskmanagementsystem.Task;
import com.example.taskmanagementsystem.model.TaskList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface TaskService {
    // For Employee (using your TaskList model)
    @GET("tasks")
    Call<List<TaskList>> getAllTask(@Header("api-key") String token);

    // For Details
    @GET("tasks/{id}")
    Call<TaskList> getTask(@Header("api-key") String token, @Path("id") int id);

    // For Manager (using Task model)
    @GET("tasks")
    Call<List<Task>> getAllTasks(@Header("api-key") String token);

    @FormUrlEncoded
    @POST("tasks")
    Call<Task> addTask(@Header("api-key") String token,
                       @Field("title") String title,
                       @Field("description") String desc,
                       @Field("assigned_to") String assigned,
                       @Field("create_date") String date,
                       @Field("status") String status);

    @FormUrlEncoded
    @POST("tasks/update/{id}")
    Call<Task> updateTask(@Header("api-key") String token, @Path("id") int id,
                          @Field("title") String title,
                          @Field("assigned_to") String assigned);

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(@Header("api-key") String token, @Path("id") int id);
}