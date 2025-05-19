package com.example.musicplayer.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApiService {

    @FormUrlEncoded
    @POST("musxfy/api/login.php")
    Call<LoginResponse> login(@Body LoginRequest request);
}
