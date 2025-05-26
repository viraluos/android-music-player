package com.example.musicplayer.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PlaylistApiService {
    @POST("create_playlist.php")
    Call<PlaylistResponse> createPlaylist(
            @Header("Authorization") String token,
            @Body PlaylistRequest request
    );

    @GET("get_user_playlists.php")
    Call<List<Playlist>> getUserPlaylists(@Header("Authorization") String token);

    @POST("add_song_to_playlist.php")
    Call<PlaylistResponse> addSongToPlaylist(
            @Header("Authorization") String token,
            @Body AddSongRequest request
    );
}