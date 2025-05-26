package com.example.musicplayer.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface AuthApiService {

    @POST("login.php")
    Call<AuthResponse> login(@Body AuthLoginRequest request);

    @POST("register.php")
    Call<AuthResponse> register(@Body AuthRegisterRequest request);

    @GET("user.php")
    Call<UserResponse> getUser(@Header("Authorization") String token);

}