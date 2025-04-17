package com.example.musicplayer.data.api;

import com.example.musicplayer.data.model.Song;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface SongApi {
    @GET("https://viraluos.com/musxfy/api/songs.php")
    Call<List<Song>> getAllSongs();
}
