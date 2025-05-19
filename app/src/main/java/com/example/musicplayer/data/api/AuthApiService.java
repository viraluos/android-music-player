package com.example.musicplayer.data.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("login.php")
    Call<AuthResponse> login(@Body AuthLoginRequest request);

    @POST("login.php")
    Call<ResponseBody> loginRaw(@Body AuthLoginRequest request);


    @POST("register.php")
    Call<AuthResponse> register(@Body AuthRegisterRequest request);
}
