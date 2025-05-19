package com.example.musicplayer.data.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SongApiService {
    @GET("musxfy/api/songs.php")
    Call<List<Song>> getAllSongs();
}

