package com.example.taskmanagementsystem.remote;

import com.example.taskmanagementsystem.model.TaskList;
import com.example.taskmanagementsystem.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface TaskService {

    @GET("users")
    Call<List<User>> getAllUsers(@Header("api-key") String token);

    @GET("tasks")
    Call<List<TaskList>> getAllTask(@Header("api-key") String token);

    @GET("tasks/{id}")
    Call<TaskList> getTask(@Header("api-key") String token, @Path("id") int id);

    @FormUrlEncoded
    @POST("tasks")
    Call<TaskList> addTask(
            @Header("api-key") String token,
            @Field("title") String title,
            @Field("description") String description,
            @Field("assigned_to") String assigned_to,
            @Field("created_task_by") String created_task_by,
            @Field("status") String status,
            @Field("finish_date") String finish_date
    );

    @FormUrlEncoded
    @POST("tasks/{id}")
    Call<TaskList> updateTask(
            @Header("api-key") String token,
            @Path("id") int id,
            @Field("title") String title,
            @Field("description") String description,
            @Field("assigned_to") String assigned_to,
            @Field("created_task_by") String created_task_by,
            @Field("finish_date") String finish_date
    );
    @FormUrlEncoded
    @POST("tasks/update-status/{id}")
    Call<TaskList> updateTaskStatus(
            @Header("api-key") String token,
            @Path("id") int id,
            @Field("status") String status,
            @Field("assigned_to") String assigned_to
    );

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(@Header("api-key") String token, @Path("id") int id);


}