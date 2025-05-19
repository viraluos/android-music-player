package com.example.musicplayer.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("login.php")
    Call<AuthResponse> login(@Body AuthLoginRequest request);

    @POST("register.php")
    Call<AuthResponse> register(@Body AuthRegisterRequest request);
}
