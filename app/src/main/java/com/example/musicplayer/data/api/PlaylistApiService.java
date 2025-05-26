package com.example.musicplayer.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PlaylistApiService {
    @POST("createPlaylist.php")
    Call<PlaylistResponse> createPlaylist(
            @Header("Authorization") String token,
            @Body PlaylistRequest request
    );

    @GET("getUserPlaylists.php")
    Call<List<Playlist>> getUserPlaylists(
            @Header("Authorization") String token
    );

    @POST("addSongToPlaylist.php")
    Call<PlaylistResponse> addSongToPlaylist(
            @Header("Authorization") String token,
            @Body AddSongRequest request
    );
}