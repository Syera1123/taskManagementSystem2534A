package com.example.taskmanagementsystem.remote;

import com.example.taskmanagementsystem.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

        // Login menggunakan email
        @FormUrlEncoded
        @POST("users/login")
        Call<User> loginEmail(
                @Field("email") String email,
                @Field("password") String password
        );

        // Login menggunakan username
        @FormUrlEncoded
        @POST("users/login")
        Call<User> loginUsername(
                @Field("username") String username,
                @Field("password") String password
        );
}
